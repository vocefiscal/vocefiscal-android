/**
 * 
 */
package org.vocefiscal.communications;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author andre
 *
 */
public class CommunicationUtils 
{


	/**
	 * test if there is some connection
	 * @param ctx
	 * @return
	 */
	public static boolean verifyConnectivity(Context ctx)
	{

		ConnectivityManager connectivityManager =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		return netInfo!=null && netInfo.isConnectedOrConnecting();
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().split("[%]")[0];
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * verify if there is a connection and a route to specific host
	 * @param ctx
	 * @param urlString
	 * @return
	 */
	public static boolean verifyConnectivityAndRouteToHost(Context ctx,String urlString)
	{

		boolean HaveConnectedWifi = false;
		boolean HaveConnectedMobile = false;
		int ip = -1;

		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();

		if(netInfo!=null && netInfo.length>0)
		{
			ip =lookupHost(urlString);
			for (NetworkInfo ni : netInfo)
			{
				if ("WIFI".equalsIgnoreCase(ni.getTypeName()))
					if (ni.isConnected() && cm.requestRouteToHost(ni.getType(), ip) )
						HaveConnectedWifi = true;
				if (!HaveConnectedWifi && "MOBILE".equalsIgnoreCase(ni.getTypeName()))
					if (ni.isConnected() && cm.requestRouteToHost(ni.getType(), ip) )
						HaveConnectedMobile = true;
			}
		}
		return HaveConnectedWifi || HaveConnectedMobile;


	}
	/**
	 * verify if there is a connection and a route to specific host
	 * @param ctx
	 * @param ip
	 * @return
	 */
	public static boolean verifyConnectivityAndRouteToHost(Context ctx,int ip)
	{

		boolean HaveConnectedWifi = false;
		boolean HaveConnectedMobile = false;


		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();

		if(netInfo!=null && netInfo.length>0)
		{
			for (NetworkInfo ni : netInfo)
			{
				if ("WIFI".equalsIgnoreCase(ni.getTypeName()))
					if (ni.isConnected() && cm.requestRouteToHost(ni.getType(), ip) )
						HaveConnectedWifi = true;
				if (!HaveConnectedWifi && "MOBILE".equalsIgnoreCase(ni.getTypeName()))
					if (ni.isConnected() && cm.requestRouteToHost(ni.getType(), ip) )
						HaveConnectedMobile = true;
			}
		}
		return HaveConnectedWifi || HaveConnectedMobile;


	}

	/**
	 * hostname from string to int
	 * @param hostname
	 * @return
	 */
	public static int lookupHost(String hostname) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			return -1;
		}
		byte[] addrBytes;
		int addr;
		addrBytes = inetAddress.getAddress();
		addr = ((addrBytes[3] & 0xff) << 24)
				| ((addrBytes[2] & 0xff) << 16)
				| ((addrBytes[1] & 0xff) << 8)
				|  (addrBytes[0] & 0xff);
		return addr;
	}


	public static boolean isWifiOr3G(Context ctx)
	{
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info==null)
			return false;

		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE
				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				) {
			return info.isConnected();
		} else {
			return false;
		}
	}
	
	public static boolean isWifi(Context ctx)
	{
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info==null)
			return false;

		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI) 
		{
			return info.isConnected();
		}else 
		{
			return false;
		}
	}
}
