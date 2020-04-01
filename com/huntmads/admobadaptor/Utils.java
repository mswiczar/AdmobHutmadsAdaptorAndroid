package com.huntmads.admobadaptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class Utils {
	
	 static String sID = null;
	 private static final String INSTALLATION = "INSTALLATION";

	 public static boolean checkPermission(Activity activity,String permission)
		{
			
		  //  String permission = "android.permission.READ_PHONE_STATE";
		    int res = activity.checkCallingOrSelfPermission(permission);
		    return res == (PackageManager.PERMISSION_GRANTED);            
		}


	public static String scrape(String resp, String start, String stop) {
		int offset, len;
		if((offset = resp.indexOf(start)) < 0)
			return "";
		if((len = resp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String scrapeIgnoreCase(String resp, String start, String stop) {
		int offset, len;
		String temp = resp.toLowerCase();
		start = start.toLowerCase();
		stop = stop.toLowerCase();
		
		if((offset = temp.indexOf(start)) < 0)
			return "";
		if((len = temp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String md5(String data) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			digester.update(data.getBytes());
			byte[] messageDigest = digester.digest();
			return Utils.byteArrayToHexString(messageDigest);
		} catch(NoSuchAlgorithmException e) {			
		}
		return null;
	}
	
	public static String byteArrayToHexString(byte[] array) {
		StringBuffer hexString = new StringBuffer();
		for (byte b : array) {
			int intVal = b & 0xff;
			if (intVal < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(intVal));
		}
		return hexString.toString();		
	}
	
	
	
	public static  int getConnectionSpeed(Context context)
	{
		Integer connectionSpeed = 0;
	
		try 
		{
			ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
			if(networkInfo != null) {
				int type = networkInfo.getType();
				int subtype = networkInfo.getSubtype();
			
			//	0 - low (gprs, edge), 1 - fast (3g, wifi)
			if(type == ConnectivityManager.TYPE_WIFI) {
				connectionSpeed = 1;
			} else if(type == ConnectivityManager.TYPE_MOBILE) {
				if(subtype == TelephonyManager.NETWORK_TYPE_EDGE) {
					connectionSpeed = 0;
				} else if(subtype == TelephonyManager.NETWORK_TYPE_GPRS) {
					connectionSpeed = 0;
				} else if(subtype == TelephonyManager.NETWORK_TYPE_UMTS) {
					connectionSpeed = 1;
				}
			}
			}
		
		}
		catch (Exception e) 
		{
		}
		return connectionSpeed;
		

	}
	
	
	public static  String getMcc(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm!=null) 
		{
			String networkOperator = tm.getNetworkOperator();      
			if ((networkOperator != null) && (networkOperator.length()>3)) 
			{         
				
				return networkOperator.substring(0, 3);   
			} 
			else
			{
				return "NULL";
			}
		}
		else
		{
			return "NULL";
		}
	}

	public static  String getMnc(Context context)
	{
		Activity activity =(Activity) context;
		if (checkPermission(activity,"android.permission.READ_PHONE_STATE"))
		{

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm!=null) 
			{
				String networkOperator = tm.getNetworkOperator();      
				if ((networkOperator != null) && (networkOperator.length()>3)) 
				{         
				
					return networkOperator.substring(3);  
				} 
				else
				{
					return "NULL";
				}
			}
			else
			{
				return "NULL";
			}
		}
		return "NULL";
	}
	
	public static String getUserID(Context context)
	{
			String deviceId=null;
			Activity activity =(Activity) context;
			if (checkPermission(activity,"android.permission.READ_PHONE_STATE"))
			{

				TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
				String temp = tm.getDeviceId();
				if (null !=  temp) deviceId = temp;
			
				else 
				{
					temp = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID); ;
					if (null != temp) deviceId = temp;
					else deviceId = null;
				}
			
			}
			if(deviceId==null)
				{
					deviceId = id(context);
				}
			String deviceIdMD5 = Utils.md5(deviceId);
			
			if((deviceIdMD5 != null) && (deviceIdMD5.length() > 0)) {
				return deviceIdMD5;			
			}
			else
			{
				return "NULL";
			}
			
	}

public synchronized static String id(Context context) {
	if (sID == null) {
		File installation = new File(context.getFilesDir(), INSTALLATION);
		try {                
			if (!installation.exists())
				writeInstallationFile(installation);
			sID = readInstallationFile(installation);
			} catch (Exception e) {
				//throw new RuntimeException(e);
				sID="1234567890";
				}
			}
	return sID;
}
private static String readInstallationFile(File installation) throws IOException {
	RandomAccessFile f = new RandomAccessFile(installation, "r");
	byte[] bytes = new byte[(int) f.length()];
	f.readFully(bytes);
	f.close();
	return new String(bytes);
}
private static void writeInstallationFile(File installation) throws IOException {
	FileOutputStream out = new FileOutputStream(installation);
	String id = UUID.randomUUID().toString();
	out.write(id.getBytes());
	out.close();
}

}
	

