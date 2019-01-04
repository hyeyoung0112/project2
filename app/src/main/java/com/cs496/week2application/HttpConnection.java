package com.cs496.week2application;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnection {
    static String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";

    private static String defaultURL = "http://143.248.140.251:9480/api";

    public static String GetAllContacts(){
        final StringBuilder sb = new StringBuilder();
        Thread sendHttpRequestThread = new Thread() {
            @Override
            public void run(){
                //Initialize variables
                HttpURLConnection httpConnection = null;
                InputStreamReader isReader = null;
                StringBuffer readTextBuf = new StringBuffer();
                BufferedReader bufReader = null;
                try {
                    // Set URL to get all contacts
                    URL url = new URL(defaultURL + "/contacts");
                    // Open HttpURLConnection of the URL and set method & timeout
                    httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("GET");
                    httpConnection.setConnectTimeout(1000);
                    httpConnection.setReadTimeout(1000);
                    // Get input stream from web url connection.
                    InputStream inputStream = httpConnection.getInputStream();
                    // Create input stream reader based on url connection input stream.
                    isReader = new InputStreamReader(inputStream);
                    // Create buffered reader.
                    bufReader = new BufferedReader(isReader);
                    // Read line of text from server response.
                    String line = bufReader.readLine();
                    // Loop while return line is not null.
                    while (line != null) {
                        // Append the text to string buffer.
                        readTextBuf.append(line);
                        sb.append(line);
                        Log.d("SERVER_CONNECTION>>>>>", "Got StringBuilder: " + sb.toString());
                        // Continue to read text line.
                        line = bufReader.readLine();
                    }
                    //JSONArray jsonArr = new JSONArray(sb.toString());
                    //if (jsonArr != null) return jsonArr;
                } catch (MalformedURLException e) {

                } catch (IOException e) {

                } finally {
                    try {
                        if (bufReader != null) {
                            bufReader.close();
                            bufReader = null;
                        }

                        if (isReader != null) {
                            isReader.close();
                            isReader = null;
                        }

                        if (httpConnection != null) {
                            httpConnection.disconnect();
                            httpConnection = null;
                        }
                    } catch (IOException ex) {
                        Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                    }
                }
            }
        };
        sendHttpRequestThread.start();
        while(sb.toString() == "");
        Log.d("SERVER_CONNECTION>>>>>", "HttpConnection will return: " + sb.toString());
        return sb.toString();
    }

    public static void AddContactToServer(final JSONObject contact){
        Thread sendHttpRequestThread = new Thread() {
            @Override
            public void run(){
                //Initialize variables
                HttpURLConnection httpConnection = null;
                try {
                    // Set URL to get all contacts
                    URL url = new URL(defaultURL + "/contacts");
                    // Open HttpURLConnection of the URL and set method & timeout
                    httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    httpConnection.setConnectTimeout(1000);

                    OutputStream os = httpConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(contact.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                } catch (MalformedURLException e) {

                } catch (IOException e) {

                } finally {
                    if (httpConnection != null) {
                        httpConnection.disconnect();
                        httpConnection = null;
                    }
                }
            }
        };
        sendHttpRequestThread.start();
        //while(THREAD IS RUNNING);

    }
}