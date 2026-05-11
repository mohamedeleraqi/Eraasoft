package at.waldviertler.datenpflege.detaillisten.webshop.v2.module.plz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;                   // JFace-Dialoge
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;                        // SWT Fallback
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import at.waldviertler.datenpflege.detaillisten.webshop.AdmWebshopModule;
import at.waldviertler.guistandardklassen.tabelle.TableElementMitDatensatz;

public class AdmWebshopV2PlzPresenter extends AdmWebshopModule<AdmWebshopV2PlzModel, AdmWebshopV2PlzView, DSAdmWebshopPlz> {

    private List<TableElementMitDatensatz> tabelleElemente = new ArrayList<TableElementMitDatensatz>();
    private DSAdmWebshopPlz aktuellerDatenSatz;

    public AdmWebshopV2PlzPresenter(AdmWebshopV2PlzModel model, AdmWebshopV2PlzView view) {
        super(model, view);
        init();                // Daten initial laden
        connectListener();     // Listener verbinden
    }

    @Override
    public void init() {
        model.createModel();   // Daten aus DB laden
        tabelleElemente.clear();

        for (DSAdmWebshopPlz ds : model.getList()) {
            TableElementMitDatensatz tds = model.addDatenSatz2Tabelle(ds);
            tabelleElemente.add(tds);
        }

        view.getTableFactory().getTableViewer().setInput(tabelleElemente); // Tabelle füllen
    }

    // Ereignisse/Buttons verbinden
    @Override
    public void connectListener() {

        // Neu: Validierung -> Überschneidung (mit Anzeige) -> speichern -> reselect + focus
        view.getButtons().bNeu.addListener(SWT.Selection, e -> {
            Control prevFocus = view.getDisplay().getFocusControl();

            DSAdmWebshopPlz neuerDs = view.erzeugeNeuenDatenSatz();

            // 1) Validation (aus DS-Helfer)
            String err = neuerDs.getValidationError();
            if (err != null) {
                warn("Ungültige Eingabe", err);
                return;
            }

            // 2) Konflikt prüfen (frische Daten!)
            AdmWebshopV2PlzModel check = new AdmWebshopV2PlzModel();
            check.createModel();
            int von = Integer.parseInt(neuerDs.getPlzVon().trim());
            int bis = Integer.parseInt(neuerDs.getPlzBis().trim());
            DSAdmWebshopPlz konflikt = check.findeUeberschneidung(neuerDs.getStaatName(), von, bis, 0);

            if (konflikt != null) {
                // Details anzeigen und im Grid selektieren
                warn("Überschneidung",
                    "Der eingegebene PLZ-Bereich überschneidet sich mit einem vorhandenen Eintrag:\n" +
                    "Staat: " + safe(konflikt.getStaatName()) + "\n" +
                    "Bereich: " + safe(konflikt.getPlzVon()) + " - " + safe(konflikt.getPlzBis()) + "\n" +
                    "Filiale: " + safe(konflikt.getKuerzel()));
                // versuche den Konflikt in der Tabelle zu markieren
                refreshPreservingTopIndex();       // sicherstellen, dass Input aktuell ist
                reselectById(konflikt.getId());    // Konfliktzeile selektieren
                return;
            }

            // 3) Speichern
            neuerDs.insert(); // ID gesetzt
            int newId = neuerDs.getId();

            // 4) UI aktualisieren
            refreshPreservingTopIndex();
            reselectById(newId);
            restoreFocusOrDefault(prevFocus);

            info("Gespeichert", "Der neue Datensatz wurde gespeichert.");
        });

        // Speichern (Update): Validierung -> Überschneidung (mit Anzeige) -> speichern -> reselect + focus
        view.getButtons().bSpeichern.addListener(SWT.Selection, e -> {
            if (aktuellerDatenSatz == null) {
                warn("Keine Auswahl", "Bitte wählen Sie zuerst einen Datensatz aus.");
                return;
            }

            if (!confirm("Änderungen bestätigen", "Möchten Sie die Änderungen speichern?")) {
                info("Abgebrochen", "Die Änderungen wurden nicht gespeichert.");
                return;
            }

            Control prevFocus = view.getDisplay().getFocusControl();
            int idToReselect = aktuellerDatenSatz.getId();

            // View -> DS
            aktualisiereDatenSatzVomView(aktuellerDatenSatz);

            // 1) Validation
            String err = aktuellerDatenSatz.getValidationError();
            if (err != null) {
                warn("Ungültige Eingabe", err);
                return;
            }

            // 2) Konflikt prüfen (frische Daten!)
            AdmWebshopV2PlzModel check = new AdmWebshopV2PlzModel();
            check.createModel();
            int von = Integer.parseInt(aktuellerDatenSatz.getPlzVon().trim());
            int bis = Integer.parseInt(aktuellerDatenSatz.getPlzBis().trim());
            DSAdmWebshopPlz konflikt = check.findeUeberschneidung(aktuellerDatenSatz.getStaatName(), von, bis, idToReselect);

            if (konflikt != null) {
                warn("Überschneidung",
                    "Der eingegebene PLZ-Bereich überschneidet sich mit einem vorhandenen Eintrag:\n" +
                    "Staat: " + safe(konflikt.getStaatName()) + "\n" +
                    "Bereich: " + safe(konflikt.getPlzVon()) + " - " + safe(konflikt.getPlzBis()) + "\n" +
                    "Filiale: " + safe(konflikt.getKuerzel()));
                // im Grid zeigen
                refreshPreservingTopIndex();
                reselectById(konflikt.getId());
                return;
            }

            // 3) Speichern
            aktuellerDatenSatz.update();

            // 4) UI aktualisieren
            refreshPreservingTopIndex();
            reselectById(idToReselect);
            restoreFocusOrDefault(prevFocus);

            info("Gespeichert", "Die Änderungen wurden gespeichert.");
        });

        // Löschen (Bestätigung + Nachauswahl)
        view.getButtons().bLoeschen.addListener(SWT.Selection, e -> {
            if (aktuellerDatenSatz == null) {
                warn("Keine Auswahl", "Bitte wählen Sie zuerst einen Datensatz aus.");
                return;
            }

            if (!confirm("Löschen bestätigen",
                    "Sind Sie sicher, dass Sie den Datensatz löschen möchten?\nDieser Vorgang kann nicht rückgängig gemacht werden.")) {
                info("Abgebrochen", "Der Datensatz wurde nicht gelöscht.");
                return;
            }

            Table table = view.getTableFactory().getTable();
            int top = table.getTopIndex();
            int selIndex = table.getSelectionIndex();

            aktuellerDatenSatz.delete();
            aktuellerDatenSatz = null;

            refresh();

            int newIndex = Math.min(selIndex, table.getItemCount() - 1);
            if (newIndex >= 0) {
                table.setSelection(newIndex);
                TableElementMitDatensatz row = getRowByIndex(newIndex);
                if (row != null) {
                    TableViewer tv = view.getTableFactory().getTableViewer();
                    tv.setSelection(new StructuredSelection(row), true);
                }
            }
            int maxTop = Math.max(0, table.getItemCount() - 1);
            table.setTopIndex(Math.min(top, maxTop));

            info("Gelöscht", "Der Datensatz wurde gelöscht.");
        });

        // Tabellen-Auswahl übernehmen
        view.getTableFactory().getTableViewer().addSelectionChangedListener(event -> {
            Object selected = ((IStructuredSelection) event.getSelection()).getFirstElement();
            if (selected instanceof TableElementMitDatensatz) {
                gleicheEingabeAb((TableElementMitDatensatz) selected);
            }
        });
    }

    @Override
    public void refresh() {
        model.createModel();   // Daten neu laden

        List<TableElementMitDatensatz> elemente = new ArrayList<TableElementMitDatensatz>();
        for (DSAdmWebshopPlz ds : model.getList()) {
            if (ds == null) continue;
            TableElementMitDatensatz tds = model.addDatenSatz2Tabelle(ds);
            if (tds != null) elemente.add(tds);
        }

        view.getTableFactory().getTableViewer().setInput(elemente); // Tabelle neu setzen
        view.getTableFactory().getTableViewer().refresh();          // Anzeige aktualisieren
    }

    // Auswahl aus Tabelle übernehmen
    public void gleicheEingabeAb(TableElementMitDatensatz element) {
        if (element == null) return;
        aktuellerDatenSatz = (DSAdmWebshopPlz) element.getDatenSatz();
        fuegeDatenSatzEin();
    }

    // Felder mit aktueller Auswahl füllen
    public void fuegeDatenSatzEin() {
        if (aktuellerDatenSatz == null) return;

        view.getTxtPlzVon().setText(aktuellerDatenSatz.getPlzVon() == null ? "" : aktuellerDatenSatz.getPlzVon());
        view.getTxtPlzBis().setText(aktuellerDatenSatz.getPlzBis() == null ? "" : aktuellerDatenSatz.getPlzBis());
        view.getTxtKuerzel().setText(aktuellerDatenSatz.getKuerzel() == null ? "" : aktuellerDatenSatz.getKuerzel());
        view.getComboStaat().setDatenSatz(aktuellerDatenSatz.getIdStaat(), 0, 0);
    }

    // Werte aus View in Datensatz schreiben
    public void aktualisiereDatenSatzVomView(DSAdmWebshopPlz ds) {
        if (ds == null) return;
        ds.setPlzVon(view.getTxtPlzVon().getText());
        ds.setPlzBis(view.getTxtPlzBis().getText());
        ds.setKuerzel(view.getTxtKuerzel().getText());

        String staatText = (view.getComboStaat().getCombo() != null)
                ? view.getComboStaat().getCombo().getText()
                : null;

        ds.setIdStaat(view.getComboStaat().getID(false, false));
        ds.setStaatName((staatText != null && !staatText.trim().isEmpty()) ? staatText : null);
    }

    @Override
    public DSAdmWebshopPlz getDs() {
        return new DSAdmWebshopPlz();
    }

    // ======== Hilfsfunktionen für Selektion/Focus/Scroll ========

    private void reselectById(int id) {
        TableViewer tv = view.getTableFactory().getTableViewer();
        Object input = tv.getInput();
        if (!(input instanceof List<?>)) return;

        @SuppressWarnings("unchecked")
        List<TableElementMitDatensatz> rows = (List<TableElementMitDatensatz>) input;
        for (TableElementMitDatensatz row : rows) {
            DSAdmWebshopPlz ds = (DSAdmWebshopPlz) row.getDatenSatz();
            if (ds != null && ds.getId() == id) {
                tv.setSelection(new StructuredSelection(row), true);
                tv.reveal(row);
                break;
            }
        }
    }

    private void refreshPreservingTopIndex() {
        Table table = view.getTableFactory().getTable();
        int top = table.getTopIndex();
        refresh();
        int maxTop = Math.max(0, table.getItemCount() - 1);
        table.setTopIndex(Math.min(top, maxTop));
    }

    private void restoreFocusOrDefault(Control prevFocus) {
        if (prevFocus != null && !prevFocus.isDisposed()) {
            prevFocus.setFocus();
        } else if (view.getTxtKuerzel() != null && !view.getTxtKuerzel().isDisposed()) {
            view.getTxtKuerzel().setFocus();
        }
    }

    private TableElementMitDatensatz getRowByIndex(int index) {
        TableViewer tv = view.getTableFactory().getTableViewer();
        Object input = tv.getInput();
        if (!(input instanceof List<?>)) return null;

        @SuppressWarnings("unchecked")
        List<TableElementMitDatensatz> rows = (List<TableElementMitDatensatz>) input;
        if (index < 0 || index >= rows.size()) return null;
        return rows.get(index);
    }

    // ===== Dialog-Hilfen =====

    private Shell shell() {
        return view != null ? view.getShell() : null;
    }

    private boolean confirm(String title, String message) {
        Shell sh = shell();
        try {
            return MessageDialog.openQuestion(sh, title, message);
        } catch (Throwable t) {
            MessageBox box = new MessageBox(sh, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            box.setText(title);
            box.setMessage(message);
            return box.open() == SWT.YES;
        }
    }

    private void info(String title, String message) {
        Shell sh = shell();
        try {
            MessageDialog.openInformation(sh, title, message);
        } catch (Throwable t) {
            MessageBox box = new MessageBox(sh, SWT.ICON_INFORMATION | SWT.OK);
            box.setText(title);
            box.setMessage(message);
            box.open();
        }
    }

    private void warn(String title, String message) {
        Shell sh = shell();
        try {
            MessageDialog.openWarning(sh, title, message);
        } catch (Throwable t) {
            MessageBox box = new MessageBox(sh, SWT.ICON_WARNING | SWT.OK);
            box.setText(title);
            box.setMessage(message);
            box.open();
        }
    }

    // Helfer: Null-sicherer Text
    private static String safe(String s) {
        return (s == null) ? "" : s;
    }
}
