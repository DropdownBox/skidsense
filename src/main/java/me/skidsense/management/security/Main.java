package me.skidsense.management.security;

import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static Encrypt asd = new Encrypt();
	public static void main(String[] args) {
	System.out.println(asd.SHA256(getCPUSerialNumber() + getHardDiskSerialNumber()));
	HWID.initLocalAuthorized();
	HWID.main();
	}


   public static String getCPUSerialNumber() {
       String serial = new String();
       try {
           Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});

           process.getOutputStream().close();

           Scanner sc = new Scanner(process.getInputStream());

           serial = sc.next();

       } catch (IOException e) {
           throw new RuntimeException("获取参数1错误");
       }
       return serial;
   }

    public static String getHardDiskSerialNumber() {
        String serial = new String();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "path", "win32_physicalmedia", "get", "serialnumber"});

            process.getOutputStream().close();

            Scanner sc = new Scanner(process.getInputStream());

            serial = sc.next();

        } catch (Exception e) {
            throw new RuntimeException("获取参数2错误");
        }

        return serial;
    }
}
