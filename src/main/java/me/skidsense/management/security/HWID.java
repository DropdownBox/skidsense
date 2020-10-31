package me.skidsense.management.security;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;

public class HWID {
	public static Encrypt asd = new Encrypt();
	private static String data;
	private static final HashSet<String> authorized = new HashSet<>();
	public static void main() {
		try {
			//建立连接
			URL url = new URL("https://gitee.com/winxpqq955/skidsensebackup/raw/master/ok_boy.txt");
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, SkidSenseHWIDAUTHENCATOR) SkidSenseHWIDAUTHENCATOR/86.0.2661.87 SkidSenseHWIDAUTHENCATOR/537.3");
			//获取输入流
			InputStream input = httpUrlConn.getInputStream();
			//将字节输入流转换为字符输入流
			InputStreamReader read = new InputStreamReader(input, "utf-8");
			//为字符输入流添加缓冲
			BufferedReader br = new BufferedReader(read);
			// 读取返回结果
			data = br.readLine();
			while(data!=null)  {
				verify();
				data=br.readLine();

			}
			// 释放资源
			br.close();
			read.close();
			input.close();
			httpUrlConn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initLocalAuthorized(){
		authorized.add(asd.SHA256(getCPUSerialNumber() + getHardDiskSerialNumber()));
	}

	private static boolean security(){
		notify("YourID");
		return true;
	}

	public static String getHWID() {
		try {
        final String main = getHardDiskSerialNumber()+getHardDiskSerialNumber().trim();

        return asd.SHA256(main);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "Error";
    }
	
	public static boolean LocalAuth(){
		return authorized.contains(asd.SHA256(getCPUSerialNumber() + getHardDiskSerialNumber())) && security();
	}

	public static void verify(){
		try {
			if (data.contains(asd.SHA256(getCPUSerialNumber() + getHardDiskSerialNumber())) && security() && !LocalAuth()) {
				System.out.println("验证成功！");
				//可以在这里开始加载event
				//
			}else {
				System.out.println("NO");
			}
		} finally {
			//可以在这里加入验证过的执行代码
			//这里的代码无论如何都会被执行
			//验证成功需要加载event
			//验证失败结束进程
			System.out.println("End");
			Runtime.getRuntime().exit(114514);
		}
	}

	public static String getCPUSerialNumber() {

		String serial = "";
		try {
			Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});

			process.getOutputStream().close();

			Scanner sc = new Scanner(process.getInputStream());

			String property = sc.next();

			serial = sc.next();

		} catch (IOException e) {
			throw new RuntimeException("获取参数1错误");
		}
		return serial;
	}

	public static String getHardDiskSerialNumber() {
		String serial = "";
		try {
			Process process = Runtime.getRuntime().exec(new String[]{"wmic", "path", "win32_physicalmedia", "get", "serialnumber"});

			process.getOutputStream().close();

			Scanner sc = new Scanner(process.getInputStream());

			String property = sc.next();

			serial = sc.next();

		} catch (Exception e) {
			throw new RuntimeException("获取参数2错误");
		}

		return serial;
	}

	public static void notify(String chatID){
		//可以在这里写个KILLSWICH或者是日志
//		String ip = request("https://api.ipify.org/");
//		request(Config.NOTIFY_ENDPOINT + "/sendMessage?chat_id="+chatID+"&text="+"Authorized Access from <code>"+ip+"</code>"+"&parse_mode=HTML");
	}
}
