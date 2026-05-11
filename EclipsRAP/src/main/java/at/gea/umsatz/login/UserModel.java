package at.gea.umsatz.login;

import java.time.Instant;

public class UserModel {
    private Long id;
    private String username;
    private boolean admin;
    private boolean active;
    private String staat;
    private String ort;
    private String plz;
    private String street;
    private String email;
    private String phone;
    private String filiale;
    private boolean partner;
    private String uid;
    private Instant creationDate;

    public UserModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getStaat() { return staat; }
    public void setStaat(String staat) { this.staat = staat; }
    
    public String getOrt() { return ort; }
    public void setOrt(String ort) { this.ort = ort; }
    
    public String getPlz() { return plz; }
    public void setPlz(String plz) { this.plz = plz; }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getFiliale() { return filiale; }
    public void setFiliale(String filiale) { this.filiale = filiale; }
    
    public boolean isPartner() { return partner; }
    public void setPartner(boolean partner) { this.partner = partner; }
    
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    
    public Instant getCreationDate() { return creationDate; }
    public void setCreationDate(Instant creationDate) { this.creationDate = creationDate; }
}