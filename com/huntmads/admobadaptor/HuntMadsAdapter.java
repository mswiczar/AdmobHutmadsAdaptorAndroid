package com.huntmads.admobadaptor;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.util.DisplayMetrics;
//import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Skeleton Adapter for AdMob Mediation
 */
public final class HuntMadsAdapter implements
    MediationBannerAdapter<HuntMadsExtras, HuntMadsServerParameters>,
    MediationInterstitialAdapter<HuntMadsExtras, HuntMadsServerParameters> {

  /*
   * Callback listeners. This class handles both in-activity (banner) and interstitial ads, so it
   * listens for both
   */
	
	 private float  factorDPI=1;
	
	
  private MediationBannerListener bannerListener;
  private MediationInterstitialListener interstitialListener;
  
  
  private ImageView iv;
  private Dialog theIntersitial;
  
  
  private int hasLocation;
  
  private Activity theAct;
  private Thread thread;
  private Runnable getAdsRunnable;
  
  
  private HuntMadsServerParameters _ServerParameters;
  private AdSize _AdSize;
  
  @SuppressWarnings("unused")
private MediationAdRequest _MediationAdRequest;
  @SuppressWarnings("unused")
  private HuntMadsExtras _Extras;
  
  
  private String ua;
  private String ua2;
  private String theClick2;
  
  
  private int conectionSpeed=0;
  private String MNC;
  private String MCC;
  private String udid;
  
  private Location aLocation;
  
  private boolean isTestDevice=false;
  private boolean isIntersitial=false;
  
  private WebView webView;
  private String thesource;
  
  
	private HashMap<String,Bitmap> urlToBitmap = new HashMap<String,Bitmap>();

  
	
	
	private InputStream OpenHttpConnection(String urlString) 
		    throws IOException
		    {
		        InputStream in = null;
		        int response = -1;
		               
		        URL url = new URL(urlString);
		        
			    //URL url = new URL("http://mswiczar.com/huntmads/1.php?");
		        
		        
		        URLConnection conn = url.openConnection();
		         
		        if (!(conn instanceof HttpURLConnection))                     
		            throw new IOException("Not an HTTP connection");
		        
		        try{
		            HttpURLConnection httpConn = (HttpURLConnection) conn;
		            httpConn.setAllowUserInteraction(false);
		            httpConn.setInstanceFollowRedirects(true);
		            httpConn.setRequestMethod("GET");
		            httpConn.setRequestProperty("User-Agent",ua2);
		       //     Log.v("Calling with User-Agent",ua2);
		            httpConn.connect(); 

		            response = httpConn.getResponseCode();                 
		            if (response == HttpURLConnection.HTTP_OK) {
		                in = httpConn.getInputStream();                                 
		            }                     
		        }
		        catch (Exception ex)
		        {
		            throw new IOException("Error connecting");            
		        }
		        return in;     
		    }
			
	private Bitmap DownloadImage(String URL)
    {        
        Bitmap bitmap = null;
        InputStream in = null;        
        try {
            in = OpenHttpConnection(URL);
            if (in != null)
            {
            	bitmap =  BitmapFactory.decodeStream(in);
            	in.close();
            }
        } catch (IOException e1) {
            
            e1.printStackTrace();
        }
        return bitmap;                
    }		


	
	private String getUrlToCall()
	{
		  String IdZone =_ServerParameters.IdZone;
		  String IdSite = _ServerParameters.IdSite;

		 int y =  _AdSize.getHeight();
		 int x = _AdSize.getWidth();
		 String user = ua;
		 String atype;
		 String aformat;
		 if (isIntersitial)
		 {
			 atype = "4";
			 aformat= "FULLSCREEN";
		 }
		 else
		 {
			 atype = "2";
			 if ((x==320) &&(y==50))   
			 {
				 aformat= "auto";
			 
			 }
			 else
			 {
				 aformat= x +"x"+y;
			 }
		 }
		 
		String request ="http://ads.huntmad.com/ad?mediationpartner=googleadmob&site="+IdSite+"&zone="+IdZone+"&count=1&key=3&size_x="+x+"&size_y="+y+"&min_size_x="+x+"&min_size_y="+y+"&type="+atype+"&ua="+user+"&udid="+udid+"&connection_speed="+conectionSpeed+"&version=2.9.0&mcc="+MCC+"&mnc="+MNC+"&format="+aformat;
			 
		 if(hasLocation==1)
		 {
			 request =request +"&lat="+aLocation.getLatitude()+"long="+aLocation.getLongitude();
		 }

		 if(isTestDevice)
		 {
			 request =request +"&test=1";
		 }
		 
		 //Log.e("REQUEST URL" ,request );
		 

		return request;
		
	}
	
	
	private void getAds(){
        try{
			
        	//Thread.sleep(3000);
        	isIntersitial=false;

            String strUrl = getUrlToCall();
         	HuntMadsXmlReader axmlreader =  new HuntMadsXmlReader();
         	axmlreader.ua = ua2;
          	axmlreader.str_Name = "mojiva";
            axmlreader.RssParserPull(strUrl);
            	  
            List<HashMap<String,String>> salida =axmlreader.parse(); 

            if (salida.size() >0)
            {
            			int hayError=0;
             	        HashMap<String,String> o = salida.get(0);
             	       
             	        if (o.get("error")!=null)
             	        {
             	        	hayError=1;
             	        }
            			
             	        if (hayError==1)
            			{
            				bannerListener.onFailedToReceiveAd(HuntMadsAdapter.this,ErrorCode.NO_FILL );
            				return;
            			}

             	       //Log.v("o: ",o.get("img"));
            	       String thestrIMAGE = o.get("img");
            	       theClick2 = 	o.get("url");
            	       Bitmap abit = urlToBitmap.get(thestrIMAGE);
                 	   if(abit==null)
                 	   {
                 		   abit = this.DownloadImage(thestrIMAGE);
                 		   if (abit==null)
                 		   {
                 			  bannerListener.onFailedToReceiveAd(HuntMadsAdapter.this,ErrorCode.INTERNAL_ERROR);
                 		   }
                 		   else
                 		   {
                 			   iv.setImageBitmap(abit);
                 			   urlToBitmap.put(thestrIMAGE, abit);
                 			   
                 			  String track =o.get("track");
                			   if (track != null)
                			   {
                				   if (track !="")
                				   {
                					   urlToBitmap.get(track);
                				   }
                			   }
                 			   bannerListener.onReceivedAd(HuntMadsAdapter.this);
                 		   }
                 	   }
            }

        } catch (Exception e) {
        	
        	try
        	{
             // Log.e("BACKGROUND_PROC", e.getMessage());
 			  bannerListener.onFailedToReceiveAd(HuntMadsAdapter.this,ErrorCode.NETWORK_ERROR);
             } catch (Exception ew) 
             {
            	

        	}
        	
          }
		} 	
	
  
	
	private void getAdsIntersitial(){
        try{
			
        	isIntersitial=true;
        	
            String strUrl = getUrlToCall();
         	HuntMadsXmlReader axmlreader =  new HuntMadsXmlReader();
          	axmlreader.str_Name = "mojiva";
          	axmlreader.ua = ua2;
            axmlreader.RssParserPull(strUrl);
            	  
            List<HashMap<String,String>> salida =axmlreader.parse(); 

            if (salida.size() >0)
            {
            			int hayError=0;
             	        HashMap<String,String> o = salida.get(0);
             	       
             	        if (o.get("error")!=null)
             	        {
             	        	hayError=1;
             	        }
            			
             	        if (hayError==1)
            			{
             	        	interstitialListener.onFailedToReceiveAd(HuntMadsAdapter.this,ErrorCode.NO_FILL );
            				return;
            			}

             	       //Log.v("o: ",o.get("img"));
             	        
            	       thesource = o.get("content");
            	       theClick2 = 	o.get("url");
            	       if (thesource.equals("")==true)
            	          {
                 			  interstitialListener.onFailedToReceiveAd(HuntMadsAdapter.this,ErrorCode.INTERNAL_ERROR);
                 		   }
                 		   else
                 		   {
                  			  String track =o.get("track");
                 			   if (track != null)
                 			   {
                 				   if (track !="")
                 				   {
                 					   urlToBitmap.get(track);
                 				   }
                 			   }

                 			  interstitialListener.onReceivedAd(HuntMadsAdapter.this);
                 		   }
            }

        } catch (Exception e) {
        	
        	try
        	{
             // Log.e("BACKGROUND_PROC", e.getMessage());
              interstitialListener.onFailedToReceiveAd(HuntMadsAdapter.this,ErrorCode.NETWORK_ERROR);
             } catch (Exception ew) 
             {
            	

        	}
        	
          }
		} 	
	

  /*
   * ------------------------------------------------------------------------
   * MediationAdapter Implementation
   * ------------------------------------------------------------------------
   */

  /*
   * These methods let the mediation layer know what data types are used for server-side
   * parameters and publisher "extras"
   */
  //@Override
  public Class<HuntMadsExtras> getAdditionalParametersType() {
    return HuntMadsExtras.class;
  }

  //@Override
  public Class<HuntMadsServerParameters> getServerParametersType() {
    return HuntMadsServerParameters.class;
  }

  /*
   * Ad Requests
   */
  
  
	private void _openUrlInExternalBrowser(Context context, String url) {
		String lastUrl = null;
		String newUrl =  url;
		URL connectURL;
		while(!newUrl.equals(lastUrl) )
		{
			lastUrl = newUrl;
			try {					
				connectURL = new URL(newUrl);					
				HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
				conn.setRequestProperty("User-Agent",ua2);
	       //     Log.v("conn Calling with User-Agent",ua2);

				
				newUrl = conn.getHeaderField("Location");
				if(newUrl==null)newUrl=conn.getURL().toString();
			} catch (Exception e) {
				newUrl = lastUrl;
			}				
		}
	//	Log.v("ClickURL",newUrl);
		Uri uri = Uri.parse(newUrl);
		if( (uri.getScheme().equals("tel") || uri.getScheme().equals("tel")))
		{
			try
			{
	        	  bannerListener.onLeaveApplication(HuntMadsAdapter.this);
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(newUrl));
				context.startActivity(intent);
			}catch (Exception e) {
			}
		}
		
		else
		{
			try
			{
	        	  bannerListener.onLeaveApplication(HuntMadsAdapter.this);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUrl));
				context.startActivity(intent);
			} catch (Exception e) {
			}
		}		
}

  

  public void requestBannerAd(MediationBannerListener listener, Activity activity,
		  HuntMadsServerParameters serverParameters, AdSize adSize,
      MediationAdRequest mediationAdRequest, HuntMadsExtras extras) {
    
	  
	  theAct = activity;
	  
      DisplayMetrics metrics = new DisplayMetrics();
	  theAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	  
	  factorDPI =  (float)metrics.densityDpi/(float)160;
	  
	  if (factorDPI < 1) 
	  {
		  factorDPI=1;
		  
	  }
	  
	// Log.v("factorDPI" ," "+factorDPI );

	  switch(metrics.densityDpi){
	     case DisplayMetrics.DENSITY_LOW:
			 //	Log.v("densityDpi" ,"DENSITY_LOW" );
	            break;
	     case DisplayMetrics.DENSITY_MEDIUM:
	    	 //	Log.v("densityDpi" ,"DENSITY_MEDIUM" );
	            break;
	     case DisplayMetrics.DENSITY_HIGH:
	    	 //	Log.v("densityDpi" ,"DENSITY_HIGH" );
	            break;
	     case DisplayMetrics.DENSITY_XHIGH:
	    	 //	Log.v("densityDpi" ,"DENSITY_XHIGH" );
             	break;
	}	  
	  
	  
	  conectionSpeed =  Utils.getConnectionSpeed(activity);

	  MNC = Utils.getMnc(activity);
	  MCC = Utils.getMcc(activity);

	  /*
	   if (Utils.checkPermission(activity,"android.permission.READ_PHONE_STATE"))
	   {
	   }
	   else
		   
	   {
			  MNC = "na";
			  MCC = "na";
	   }
	   */
		

	  
      udid = Utils.getUserID(activity);
      
      
      
      
      isTestDevice = 	mediationAdRequest.isTesting();
   //   Log.e("isTestDevice !!!","" +isTestDevice);
      aLocation = mediationAdRequest.getLocation();
      if (aLocation !=null)
      {
    	  hasLocation =1;
      }
	  ua = 	 new WebView(theAct).getSettings().getUserAgentString();
	  ua2 =ua; 
	  try
	  {
		  ua= URLEncoder.encode(ua, "UTF-8");
		   
		  //				Log.v("ua:",ua);
	  }  
	  catch (Exception e)
	  {
		  
	  }

	  _MediationAdRequest =mediationAdRequest;
	  _AdSize =adSize;
	  _ServerParameters =serverParameters;
	  _Extras=extras;
	  
	  bannerListener = listener;
	  

	  int width = _AdSize.getWidth();
	  int height = _AdSize.getHeight();
	  
	  width = (int) ((float)width * factorDPI);
	  height = (int) ((float)height * factorDPI);
	  
			  
	  iv = new ImageView(activity);
	  iv.setScaleType(ImageView.ScaleType.FIT_XY);
	  iv.setLayoutParams(new GridView.LayoutParams(width,height));

	  
	  
	  iv.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) 
          {
        	  bannerListener.onClick(HuntMadsAdapter.this);
        	  _openUrlInExternalBrowser(theAct,theClick2);

          }
      });
	  
	  getAdsRunnable = new Runnable(){
	           
	           public void run() {
	               getAds();
	           }
	       };	
	  
		thread =  new Thread(null, getAdsRunnable, "getAdsRunnable");
		thread.start();
	  
  }

  
  
  //@Override
  public void requestInterstitialAd(
      MediationInterstitialListener listener, Activity activity,
      HuntMadsServerParameters serverParameters, MediationAdRequest mediationAdRequest,
      HuntMadsExtras extras) {
	  
    interstitialListener = listener;
    
    
    
    
	  theAct = activity;
	  conectionSpeed =  Utils.getConnectionSpeed(activity);
	  

	  MNC = Utils.getMnc(activity);
	  MCC = Utils.getMcc(activity);
/*
	   if (checkPermission(activity,"android.permission.READ_PHONE_STATE"))
	   {
	   }
	   else
		   
	   {
			  MNC = "na";
			  MCC = "na";
	   }
	   */
	  

	  
    udid = Utils.getUserID(activity);
    isTestDevice = 	mediationAdRequest.isTesting();
    aLocation = mediationAdRequest.getLocation();
    if (aLocation !=null)
    {
  	  hasLocation =1;
    }
	  ua = 	 new WebView(theAct).getSettings().getUserAgentString();
	  try
	  {
		  ua= URLEncoder.encode(ua, "UTF-8");
		  //				Log.v("ua:",ua);
	  }  
	  catch (Exception e)
	  {
		  
	  }


	  _MediationAdRequest =mediationAdRequest;
	  
	  
	  Display display = activity.getWindowManager().getDefaultDisplay(); 
	  int width = display.getWidth();
	  int height = display.getHeight();
	  
	  _AdSize = new AdSize( width,  height);
	  
	  _ServerParameters =serverParameters;
	  _Extras=extras;
	  
	  
	  
	  getAdsRunnable = new Runnable(){
	           
	           public void run() {
	        	   getAdsIntersitial();
	           }
	       };	
	  
		thread =  new Thread(null, getAdsRunnable, "getAdsRunnable");
		thread.start();
    
    
    
    theIntersitial = new Dialog(activity, android.R.style.Theme_NoTitleBar);
    theAct = activity;
    
	RelativeLayout mainLayout = new RelativeLayout(theAct);
	mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

	webView = new WebView(activity);
	//webView.setId(ID_WEB);
	webView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1f));
	//webView.loadUrl("http://www.google.com");
	
	

	webView.getSettings().setJavaScriptEnabled(true);
	webView.getSettings().setBuiltInZoomControls(true);
	mainLayout.addView(webView);


	
	webView.setWebViewClient(new WebViewClient(){
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);				
			return true;
		}
		
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
	});
	
	


	Button closeButton = new Button(theAct);
		closeButton.setText("Close");
		RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		closeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		closeButton.setLayoutParams(closeLayoutParams);

	closeButton.setOnClickListener(new View.OnClickListener(){
		public void onClick(View view) {
			interstitialListener.onDismissScreen(HuntMadsAdapter.this);
			theIntersitial.dismiss();
			
		}
	});
	mainLayout.addView(closeButton);
	theIntersitial.setContentView(mainLayout);

	//interstitialListener.onReceivedAd(HuntMadsAdapter.this);

  }


  
  
  //@Override
  public void showInterstitial() {
	  	interstitialListener.onPresentScreen(HuntMadsAdapter.this);
	  	thesource = "<html><head> <meta name=\"viewport\" content=\"width=device-width,minimum-scale=1.0, maximum-scale=10.0\" /></head><body style=\"text-align:center;margin:0;padding:0\">"+thesource +"</body></html>";
	  	
	  	webView.loadData(thesource, "text/html", null);
	  	webView.getSettings().setUseWideViewPort(true);
	  	webView.getSettings().setLoadWithOverviewMode(false);
	    theIntersitial.show();
  }
  
  
  

  //@Override
  public void destroy() {
    // TODO: Clean up anything that's needed here.

	  	thread=null;  
	    bannerListener = null;
	    interstitialListener = null;

	  
  }

  //@Override
  public View getBannerView() {
	  return iv;
  }
}
