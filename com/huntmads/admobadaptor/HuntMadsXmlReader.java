package com.huntmads.admobadaptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

//import android.util.Log;
import android.util.Xml;



public class HuntMadsXmlReader
{
    private URL rssUrl;
    public String str_Name;
    public String ua;
    
    public void RssParserPull(String url)
    {
        try
        {
         //     this.rssUrl = new URL("http://mswiczar.com/huntmads/1.php?url="+url);
            this.rssUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public List<HashMap<String,String>> parse()
    {
        List<HashMap<String,String>> noticias = null;
        XmlPullParser parser = Xml.newPullParser();
 
        try
        {
            parser.setInput(this.getInputStream(), null);
 
            int evento = parser.getEventType();
 
            HashMap<String,String> noticiaActual = null;
            String etiqueta = null;
 
            while (evento != XmlPullParser.END_DOCUMENT)
            {
 
                switch (evento)
                {
                    case XmlPullParser.START_DOCUMENT:
                    	//Log.e("xml", "START_DOCUMENT");
                        noticias = new ArrayList<HashMap<String,String>>();
                        break;
 
                    case XmlPullParser.START_TAG:
                    	//Log.e("xml", "START_TAG");

                        etiqueta = parser.getName();
                    //	Log.e("xml etiqueta", etiqueta);

                        if (etiqueta.equals(str_Name))
                        {
                            noticiaActual = new HashMap<String,String>();
                        }
                        else if (noticiaActual != null)
                        {
                            noticiaActual.put(etiqueta,parser.getText());
                        }
                        break;
                        
                        
                    case XmlPullParser.TEXT:
                    //	Log.e("xml", "TEXT");
                    	String atext = parser.getText();
                   // 	Log.e(etiqueta, atext);
                        noticiaActual.put(etiqueta,atext);

                    	
                    	break;
 
                    case XmlPullParser.END_TAG:
                    //	Log.e("xml", "END_TAG");

                        etiqueta = parser.getName();
 
                        
                        
                        if (etiqueta.equals(str_Name) && noticiaActual != null)
                        {
                            noticias.add(noticiaActual);
                        }
                        break;
                }
 
                evento = parser.next();
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
 
        return noticias;
    }
 
    
    
    
    
    
    private InputStream getInputStream()
    {
        try
        {
          //  return rssUrl.openConnection().getInputStream();
            
            
	        InputStream in = null;
	        int response = -1;

	        URLConnection conn = rssUrl.openConnection();
	         
	        if (!(conn instanceof HttpURLConnection))                     
	            throw new IOException("Not an HTTP connection");
	        
	        try{
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setRequestMethod("GET");
	            httpConn.setRequestProperty("User-Agent",ua);
	         //   Log.v("Calling with User-Agent",ua);
	            httpConn.connect(); 

	            response = httpConn.getResponseCode();                 
	            if (response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();                                 
	            }                     
	        }
	        catch (IOException ex)
	        {
	            throw new RuntimeException(ex);
	        }
	        return in;
            
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        } 
            
            
            
    }

    
/*    
    private InputStream getInputStream()
    {
        try
        {
            return rssUrl.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    */
}

