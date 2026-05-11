package at.waldviertler.datenpflege.detaillisten.webshop.v2.module.plz;

import at.waldviertler.datenbank.SqlAbfrage;
import at.waldviertler.fundi.libs.core.datenbank.datensatz.DatenSatz;

public class DSAdmWebshopPlz extends DatenSatz {
    private int id;
    private int idStaat;
    private int idFiliale;
    private String staatName;
    private String kuerzel;
    private String plzVon;
    private String plzBis;


    @Override
    public void delete() {
        SqlAbfrage abfrage = new SqlAbfrage();
        String query = "delete from t_plz_kuerzel where id = ?";
        abfrage.bereiteStatementVor(query, true);
        abfrage.neuerIntParameter(id);
        abfrage.fuehreAus();
    }

    @Override
    public void insert() { 
    	save(); 
    	}

    @Override
    public void update() { 
    	save(); 
    	}

    public void save() {
        // Hinweis: Presenter macht Validation + Overlap-Check vor dem Speichern
        int von = Integer.parseInt(plzVon.trim());
        int bis = Integer.parseInt(plzBis.trim());

        SqlAbfrage abfrage = new SqlAbfrage();
        String query;
        if (id == 0) {
            query = "insert into t_plz_kuerzel(" +
                    " id_staat, kuerzel, plz_von, plz_bis, id_filiale) values(?, ?, ?, ?, ?)";
        } else {
            query = "update t_plz_kuerzel set" +
                    " id_staat = ?" +
                    ", kuerzel = ?" +
                    ", plz_von = ?" +
                    ", plz_bis = ?" +
                    ", id_filiale = ?" +
                    " where id = ? ";
        }

        abfrage.bereiteStatementVor(query, true);
        abfrage.neuerStringParameter(staatName);
        abfrage.neuerStringParameter(kuerzel);
        abfrage.neuerIntParameter(von);
        abfrage.neuerIntParameter(bis);
        abfrage.neuerIntParameter(idFiliale);

        if (id == 0) {
            this.id = abfrage.fuehreAus(); // neue ID
        } else {
            abfrage.neuerIntParameter(id);
            abfrage.fuehreAus();
        }
    }


    /** return null wenn ok, sonst Fehlermeldung (DE) */
    public String getValidationError() {
        if (staatName == null || staatName.trim().isEmpty()) {
            return "Bitte wählen Sie einen Staat aus.";
        }
        if (plzVon == null || plzBis == null) {
            return "PLZ Von/Bis darf nicht leer sein.";
        }
        try {
            Integer.parseInt(plzVon.trim());
            Integer.parseInt(plzBis.trim());
        } catch (NumberFormatException e) {
            return "PLZ Von/Bis muss eine Zahl sein.";
        }
        int von = Integer.parseInt(plzVon.trim());
        int bis = Integer.parseInt(plzBis.trim());
        if (von > bis) {
            return "PLZ Von darf nicht größer als PLZ Bis sein.";
        }
        return null;
    }

    /** Prüft Überschneidung gegen frisches Model (DB). */
    public boolean hasOverlapInDb() {
        AdmWebshopV2PlzModel check = new AdmWebshopV2PlzModel();
        check.createModel();
        int von = Integer.parseInt(plzVon.trim());
        int bis = Integer.parseInt(plzBis.trim());
        return check.hatPlzUeberschneidung(staatName, von, bis, id);
    }


    public int getId() { 
    	return id; 
    	}
    public void setId(int id) { 
    	this.id = id; 
    	}

    public int getIdStaat() { 
    	return idStaat; 
    	}
    public void setIdStaat(int idStaat) { 
    	this.idStaat = idStaat; 
    	}

    public String getStaatName() { 
    	return staatName; 
    	}
    public void setStaatName(String staatName) { 
    	this.staatName = staatName; 
    	}

    public String getKuerzel() { 
    	return kuerzel; 
    	}
    public void setKuerzel(String kuerzel) { 
    	this.kuerzel = kuerzel; 
    	}

    public String getPlzVon() { 
    	return plzVon; 
    	}
    public void setPlzVon(String plzVon) { 
    	this.plzVon = plzVon; 
    	}

    public String getPlzBis() { 
    	return plzBis; 
    	}
    public void setPlzBis(String plzBis) { 
    	this.plzBis = plzBis; 
    	}

	public int getIdFiliale() {
		return idFiliale;
	}

	public void setIdFiliale(int idFiliale) {
		this.idFiliale = idFiliale;
	}
    
}
