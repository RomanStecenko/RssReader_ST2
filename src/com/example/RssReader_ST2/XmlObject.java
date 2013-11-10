package com.example.RssReader_ST2;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class XmlObject extends AsyncTask<URL, String, ArrayList<ElementRss>> {
    final String log = "mylog";
    public static  ArrayList<ElementRss>  rssArrayList = null;

    @Override
    protected ArrayList<ElementRss> doInBackground(URL... urls) {

        try {
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) urls[0].openConnection();
            // urlConnection.setRequestMethod("GET");
            // urlConnection.setDoOutput(true);
            urlConnection.connect();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(urlConnection.getInputStream());


             rssArrayList = new ArrayList<ElementRss>();

            NodeList nodes = doc.getElementsByTagName("item");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                NodeList title = element.getElementsByTagName("title");
                NodeList link = element.getElementsByTagName("link");
                NodeList description = element.getElementsByTagName("description");
                NodeList pubDate = element.getElementsByTagName("pubDate");

                Element eTitle = (Element) title.item(0); //
                Element eLink = (Element) link.item(0);
                Element eDescription = (Element) description.item(0);
                Element ePubDate = (Element) pubDate.item(0);

                ElementRss elementRss = new ElementRss(eTitle,eLink,eDescription,ePubDate);
//
                rssArrayList.add(elementRss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return rssArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<ElementRss> s) {
       // Log.d(log, s+");
    }
}
