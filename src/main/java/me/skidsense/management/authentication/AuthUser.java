package me.skidsense.management.authentication;

public class AuthUser {
	
	public String username;
	public String hashedpassword;
	public String hwid;
	
	public AuthUser(String username ,String hashedpassword , String hwid) {
		this.username = username;
		this.hashedpassword = hashedpassword;
		this.hwid = hwid;
	}
	
    public String getUsername() {
        return this.username;
    }

    
    public String getHashedPassword() {
        return this.hashedpassword;
    }

    public String getHWID() {
        return this.hwid;
    }

}
