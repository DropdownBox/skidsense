package me.skidsense.management.authentication;

import java.net.URL;
import java.util.Scanner;

import me.skidsense.Client;
import me.skidsense.management.security.HWID;


public class Margele {
	
    public static String name;
    public static String code;
    private String hwid;
    
    public boolean auth(String code) throws Exception {
        URL url = new URL(new String(new byte[]{104 , 116 , 116 , 112 , 115 , 58 , 47 , 47 , 112 , 97 , 115 , 116 , 101 , 98 , 105 , 110 , 46 , 99 , 111 , 109 , 47 , 114 , 97 , 119 , 47 , 87 , 116 , 69 , 122 , 88 , 120 , 80 , 86}));
        Scanner s = new Scanner(url.openStream());
        this.hwid = HWID.getHWID();
        this.code = code;

        while (s.hasNext()) {
            String[] s2 = s.nextLine().split(":");
            name = s2[0];
            if ((hwid.equalsIgnoreCase(s2[1])) && (this.code.equalsIgnoreCase(s2[2]))) {
                System.out.println("Welcome " + s2[0]);
                return true;
            }
        }
        System.out.println("Login Failed");
        return false;
    }
    
	public void auth(AuthUser uAuthUser) {
		try {
			URL link = new URL(new String(new byte[]{104 , 116 , 116 , 112 , 115 , 58 , 47 , 47 , 112 , 97 , 115 , 116 , 101 , 98 , 105 , 110 , 46 , 99 , 111 , 109 , 47 , 114 , 97 , 119 , 47 , 87 , 116 , 69 , 122 , 88 , 120 , 80 , 86}));
			Scanner scanner = new Scanner(link.openStream());
	        while (scanner.hasNext()) {
	        	String[] dec = Crypto.decrypt(AuthenticationUtil.getSecretNew(), scanner.nextLine().trim()).split(":");
	        	for (int i = 0; i < dec.length; i++) {
	        		System.out.println(dec[i]);
	        		AuthUser dasdAuthUser = new AuthUser(dec[0], dec[1], dec[2]);
	        		Client.instance.tempAuthUsers.add(dasdAuthUser);
	        		dasdAuthUser = null;
				}
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
