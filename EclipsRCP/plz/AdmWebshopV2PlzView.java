package at.waldviertler.datenpflege.detaillisten.webshop.v2.module.plz;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import at.waldviertler.datenpflege.DatenpflegeButtons;
import at.waldviertler.datenpflege.detaillisten.webshop.AdmWebshopModuleView;
import at.waldviertler.fundi.libs.core.exceptions.FalscherDatenTypUebergeben;
import at.waldviertler.guistandardklassen.tabelle.TableFactory;
import at.waldviertler.lookupcombos.DBLookupCombo;
import at.waldviertler.lookupcombos.FactoryDBLookupCombo;
import at.waldviertler.lookupcombos.TypenDBLookupCombo;
import at.waldviertler.lookupdialoge.filialen.DBLookupDialogFilialen;
import at.waldviertler.ressourcen.ColorMaterialDesign;

public class AdmWebshopV2PlzView extends AdmWebshopModuleView {

    private SashForm sashBody;
    private DBLookupCombo comboStaat;
    private Text txtPlzVon;
    private Text txtPlzBis;
    private Text txtKuerzel;
    private Button btnZeigeFilialenAn;
    private TableFactory tablefactory;
    private DatenpflegeButtons buttons;
    private DBLookupDialogFilialen ldFilialen;

    private final static Color COL_BG = ColorMaterialDesign.get(ColorMaterialDesign.Background.WHITE);
    private final static Color COL_CONTROL_BG = ColorMaterialDesign.get(ColorMaterialDesign.Background.WHITE);

    public AdmWebshopV2PlzView(FormToolkit toolkit, Composite parent, int style) {
        super(toolkit, parent, style);
        erzeugeAnzeige(toolkit, this, style);
    }

    // Anzeige erstellen (Root-Layout setzen)
    public void erzeugeAnzeige(FormToolkit toolkit, Composite parent, int style) {
        parent.setLayout(new GridLayout(1, false));
        parent.setBackground(COL_BG);
        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        createBody(toolkit, parent, style);
    }

    // Hauptlayout (links: Tabelle, rechts: Eingabe & Buttons)
    private void createBody(FormToolkit toolkit, Composite parent, int style) {
        sashBody = new SashForm(parent, SWT.HORIZONTAL);
        sashBody.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        sashBody.setSashWidth(5);

        // Tabelle (links)
        Composite cTable = toolkit.createComposite(sashBody, SWT.BORDER);
        cTable.setBackground(COL_CONTROL_BG);
        cTable.setLayout(new GridLayout(3, false));
        cTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        createTableComposite(toolkit, cTable);

        // Eingabefelder + Buttons (rechts)
        Composite cBody = toolkit.createComposite(sashBody, SWT.BORDER);
        cBody.setBackground(COL_CONTROL_BG);
        cBody.setLayout(new GridLayout(2, false));
        cBody.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        createInputComposite(toolkit, cBody);

        // Buttons erzeugen
        buttons = new DatenpflegeButtons();
        Composite cButtons = buttons.erzeugeButtons(cBody, toolkit);
        cButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        sashBody.setWeights(new int[] { 70, 30 });
    }

    // Tabellenspalten definieren 
    private void createTableComposite(FormToolkit toolkit, Composite parent) {
        tablefactory = new TableFactory(null, toolkit, parent, true, false, true);
        tablefactory.addSpalte("Staat", 30, false, 0);
        tablefactory.addSpalte("Kurzel", 20, false, 0);
        tablefactory.addSpalte("PLZ Von", 20, false, 0);
        tablefactory.addSpalte("PLZ Bis", 20, false, 0);
        tablefactory.addSpalte("Filiale", 20, false, 0);


        GridData gdTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdTable.horizontalSpan = 3;
        tablefactory.getTable().setLayoutData(gdTable);

        GridData gdFilter = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdFilter.horizontalSpan = 3;
        tablefactory.getFilterComposite().setLayoutData(gdFilter);

        GridData gdSearch = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdSearch.horizontalSpan = 3;
        tablefactory.getTextSearchFilter().getTextWidget().setLayoutData(gdSearch);
    }

 // Eingabefelder erzeugen
    private void createInputComposite(FormToolkit toolkit, Composite parent) {
        FactoryDBLookupCombo comboFactory = new FactoryDBLookupCombo();

        // Staat (Combo)
        parentLayoutText(toolkit, parent, "Staat");
        try {
            comboStaat = comboFactory.erzeugeCombo(TypenDBLookupCombo.Staat, parent, null);
        } catch (FalscherDatenTypUebergeben e) {
            e.printStackTrace();
        }

        comboStaat.initialisiereCombo(); // keine feste Vorauswahl

        if (comboStaat != null && comboStaat.getCombo() != null) {
            comboStaat.getCombo().setLayoutData(fixedFieldWidth(145));
        }

        
        // PLZ Von
        parentLayoutText(toolkit, parent, "PLZ Von");
        txtPlzVon = new Text(parent, SWT.BORDER);
        txtPlzVon.setLayoutData(fixedFieldWidth(145));

        // PLZ Bis
        parentLayoutText(toolkit, parent, "PLZ Bis");
        txtPlzBis = new Text(parent, SWT.BORDER);
        txtPlzBis.setLayoutData(fixedFieldWidth(145));

        // Kürzel
        parentLayoutText(toolkit, parent, "Kurzel");
        txtKuerzel = new Text(parent, SWT.BORDER);
        txtKuerzel.setLayoutData(fixedFieldWidth(145));
        
        parentLayoutText(toolkit, parent, "Filialen");
        btnZeigeFilialenAn = new Button(parent, SWT.PUSH);
		btnZeigeFilialenAn.setText("Filiale Auswählen");
		btnZeigeFilialenAn.setLayoutData(fixedFieldWidth(165));
		ldFilialen = new DBLookupDialogFilialen(btnZeigeFilialenAn.getShell(), true, true);
		}

	 // Label mit fester Breite
	    private void parentLayoutText(FormToolkit toolkit, Composite parent, String text) {
	        Label label = new Label(parent, SWT.NONE);
	        label.setText(text);
	        label.setLayoutData(fixedFieldWidth(100));
	    }
	
	    // Hilfsfunktion: feste Breite für Eingabefelder
	    private GridData fixedFieldWidth(int width) {
	        GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
	        gd.widthHint = width;
	        return gd;
	    }
//
//    // === Auto-Grow: Breite dynamisch (Einzeilige Textfelder) ===
//    private void attachAutoGrowWidth(final Text text, final int minWidth, final int maxWidth) {
//        // initiale Anpassung
//        resizeTextWidth(text, minWidth, maxWidth);
//        // bei jeder Texteingabe neu berechnen
//        text.addModifyListener(e -> resizeTextWidth(text, minWidth, maxWidth));
//    }
//
//    // tatsächliches Anpassen der Breite basierend auf Textlänge
//    private void resizeTextWidth(final Text text, final int minWidth, final int maxWidth) {
//        if (text == null || text.isDisposed()) return;
//
//        GC gc = new GC(text); // Breitenmessung mit aktueller Schrift
//        try {
//            String s = text.getText();
//            if (s == null) s = "";
//            Point ext = gc.textExtent(s.isEmpty() ? " " : s); // mind. 1 Zeichen
//            int padding = 20;                                  // kleiner Puffer
//            int target = clamp(ext.x + padding, minWidth, maxWidth);
//
//            Object ld = text.getLayoutData();
//            GridData gd = (ld instanceof GridData)
//                    ? (GridData) ld
//                    : new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
//
//            gd.widthHint = target;
//            text.setLayoutData(gd);
//
//            // Parent neu layouten, damit die Änderung sichtbar wird
//            Composite parent = text.getParent();
//            if (parent != null && !parent.isDisposed()) {
//                parent.layout(true, true);
//            }
//        } finally {
//            gc.dispose(); // GC sauber freigeben
//        }
//    }
//
//    // Hilfsfunktion: Grenzen anwenden
//    private int clamp(int v, int min, int max) {
//        return Math.max(min, Math.min(max, v));
//    }

    // Getter
    public DBLookupCombo getComboStaat() { 
    	return comboStaat; 
    	}
    public Text getTxtPlzVon() { 
    	return txtPlzVon; 
    	}
    public Text getTxtPlzBis() { 
    	return txtPlzBis; 
    	}
    public Text getTxtKuerzel() { 
    	return txtKuerzel; 
    	}
    public TableFactory getTableFactory() { 
    	return tablefactory; 
    	}
    public DatenpflegeButtons getButtons() { 
    	return buttons; 
    	}

    public DBLookupDialogFilialen getLdFilialen() {
		return ldFilialen;
	}
    public Button getBtnZeigeFilialenAn() {
		return btnZeigeFilialenAn;
	}
    // Datensatz aus Eingabefeldern erstellen
    public DSAdmWebshopPlz erzeugeNeuenDatenSatz() {
        DSAdmWebshopPlz ds = new DSAdmWebshopPlz();

        String staatText = (comboStaat != null && comboStaat.getCombo() != null)
                ? comboStaat.getCombo().getText() : null;

        ds.setIdStaat(comboStaat.getID(false, false));
        ds.setStaatName((staatText != null && !staatText.trim().isEmpty()) ? staatText : null);
        ds.setPlzVon(txtPlzVon != null ? txtPlzVon.getText() : null);
        ds.setPlzBis(txtPlzBis != null ? txtPlzBis.getText() : null);
        ds.setKuerzel(txtKuerzel != null ? txtKuerzel.getText() : null);
        ds.setIdFiliale(ldFilialen != null ? ldFilialen.getIdPerson() : null);

        return ds;
    }
}
