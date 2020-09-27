package me.skidsense.management.authentication;

public class AuthUser {
	
	public String username;
	public String password;
	public String hashedpassword;
	public String hwid;
	
	public AuthUser(String username , String password , String hashedpassword , String hwid) {
		this.username = username;
		this.password = password;
		this.hashedpassword = password;
		this.hwid = hwid;
	}
	
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
    
    public String getHashedPassword() {
        return this.hashedpassword;
    }

    public String getHWID() {
        return this.hwid;
    }

}
