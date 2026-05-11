package at.waldviertler.datenpflege.detaillisten.webshop.v2.module.plz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import at.waldviertler.datenbank.SqlAbfrage;
import at.waldviertler.datenpflege.detaillisten.webshop.AdmWebshopModuleModel;
import at.waldviertler.fundi.libs.core.datenbank.datensatz.DatenSatz;
import at.waldviertler.guistandardklassen.tabelle.TableElementMitDatensatz;

public class AdmWebshopV2PlzModel extends AdmWebshopModuleModel  {


	private int idPostleitzahlen;
	private List<DSAdmWebshopPlz> listPlz = new ArrayList<DSAdmWebshopPlz>();

	public AdmWebshopV2PlzModel() { }

	public void createModel() {
		listPlz.clear();
	    SqlAbfrage abfrage = new SqlAbfrage();


	    String query =
	    	    "select tpk.id " +
	    	    "     , ts.id as id_staat " +
	    	    "     , ts.staat     as staat_name " +
	    	    "     , tpk.kuerzel " +
	    	    "     , tpk.plz_von " +
	    	    "     , tpk.plz_bis " +
	    	    "     , tpk.id_filiale " +
	    	    "  from t_plz_kuerzel tpk " +
	    	    "  left join t_staat ts on ts.staat = tpk.id_staat " +
	    	    " order by tpk.id_staat";

	    abfrage.bereiteStatementVor(query, false);
	    abfrage.fuehreAbfrageAus(rs -> {
	        DSAdmWebshopPlz ds = new DSAdmWebshopPlz();
	        ds.setId(rs.getInt("id"));
	        ds.setIdStaat(rs.getInt("id_staat"));
	        ds.setStaatName(rs.getString("staat_name"));
	        ds.setKuerzel(rs.getString("kuerzel"));
	        ds.setPlzVon(rs.getString("plz_von"));
	        ds.setPlzBis(rs.getString("plz_bis"));
	        ds.setIdFiliale(rs.getInt("id_filiale"));
	        listPlz.add(ds);
	        addDatenSatz2Tabelle(ds);
	    });
	}

	// Datensatz für Tabelle aufbereiten (Spaltenreihenfolge definieren)
	public TableElementMitDatensatz addDatenSatz2Tabelle(DatenSatz ds) {
	    DSAdmWebshopPlz dsNeu = (DSAdmWebshopPlz) ds;
	    TableElementMitDatensatz tds = new TableElementMitDatensatz();
	    tds.setDatenSatz(dsNeu);

	    // 1: Staat (Anzeigename)
	    tds.addSpalte(dsNeu.getStaatName() == null ? "" : dsNeu.getStaatName());
	    // 2: Filiale/Kürzel
	    tds.addSpalte(dsNeu.getKuerzel() == null ? "" : dsNeu.getKuerzel());
	    // 3: PLZ von
	    tds.addSpalte(dsNeu.getPlzVon() == null ? "" : dsNeu.getPlzVon());
	    // 4: PLZ bis
	    tds.addSpalte(dsNeu.getPlzBis() == null ? "" : dsNeu.getPlzBis());
	    
	    tds.addSpalte(dsNeu.getIdFiliale() == 0 ? "" : dsNeu.getIdFiliale());

	    return tds;
	}

	// Prüfen, ob PLZ-Bereich sich überschneidet (gleiches Land, optional ID ausschließen)
	public boolean hatPlzUeberschneidung(String staatName, int neuerVon, int neuerBis, int excludeId) {
	    for (DSAdmWebshopPlz ds : listPlz) {
	        if (!StringUtils.equals(ds.getStaatName(), staatName)) continue;
	        if (ds.getId() == excludeId) continue;

	        int existVon = Integer.parseInt(ds.getPlzVon());
	        int existBis = Integer.parseInt(ds.getPlzBis());

	        if (neuerVon <= existBis && neuerBis >= existVon) {
	            return true;
	        }
	    }
	    return false;
	}
	// Gibt den ersten überschneidenden Datensatz zurück; sonst null
	public DSAdmWebshopPlz findeUeberschneidung(String staatName, int neuerVon, int neuerBis, int excludeId) {
	    for (DSAdmWebshopPlz ds : listPlz) {
	        if (ds == null) continue;
	        if (!StringUtils.equals(ds.getStaatName(), staatName)) continue;
	        if (ds.getId() == excludeId) continue;

	        int existVon;
	        int existBis;
	        try {
	            existVon = Integer.parseInt(ds.getPlzVon());
	            existBis = Integer.parseInt(ds.getPlzBis());
	        } catch (NumberFormatException nfe) {
	            continue;
	        }

	        // Intervall-Überschneidung
	        if (neuerVon <= existBis && neuerBis >= existVon) {
	            return ds;
	        }
	    }
	    return null;
	}


	public int getIdPostleitzahlen() { 
		return idPostleitzahlen; 
		}
	
	public void setIdPostleitzahlen(int idPostleitzahlen) { 
		this.idPostleitzahlen = idPostleitzahlen; 
		}

	public List<DSAdmWebshopPlz> getList() { 
		return listPlz; 
		}
}
