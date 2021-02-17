package com.taxiappclone.common.utils;

import android.content.Context;
import android.util.Log;

import com.taxiappclone.common.app.AppConfig;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;


public final class ServerUtilities {
    private static final String TAG = ServerUtilities.class.getSimpleName();
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    public static final String POST = "post";
    public static final String GET = "get";
    public static final String PUT = "put";
    public static final String DELETE = "delete";

    public static String sendFCMMessage(JSONObject message)
    {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "key=AIzaSyDDK8QH8HcCAG-4W37muBJzbE7ug_9Y8Ec");
        return getJsonResponse(POST, AppConfig.URL_FCM, message, headers);
    }

    public static String getJsonResponse(String method, String url, JSONObject jsonObject, Map<String,String> headers){
        InputStream inputStream = null;
        HttpResponse httpResponse = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            switch (method)
            {
                case POST:
                    HttpPost httpPost = new HttpPost(url);
                    if(jsonObject!=null) {
                        StringEntity se = new StringEntity(jsonObject.toString());
                        httpPost.setEntity(se);
                    }
                    if(headers!=null) {
                        for (String header : headers.keySet()) {
                            String key = header.toString();
                            String value = headers.get(header).toString();
                            httpPost.setHeader(key, value);
                        }
                    }
                    httpResponse = httpclient.execute(httpPost);
                    break;
                case GET:
                    HttpGet httpGet = new HttpGet(url);
                    if(headers!=null) {
                        for (String header : headers.keySet()) {
                            String key = header.toString();
                            String value = headers.get(header).toString();
                            httpGet.setHeader(key, value);
                        }
                    }
                    httpResponse = httpclient.execute(httpGet);
                    break;
                case PUT:
                    HttpPut httpPut = new HttpPut(url);
                    if(jsonObject!=null) {
                        StringEntity se = new StringEntity(jsonObject.toString());
                        httpPut.setEntity(se);
                    }
                    if(headers!=null) {
                        for (String header : headers.keySet()) {
                            String key = header.toString();
                            String value = headers.get(header).toString();
                            httpPut.setHeader(key, value);
                        }
                    }
                    httpResponse = httpclient.execute(httpPut);
                    break;
                case DELETE:
                    HttpDelete httpDelete = new HttpDelete(url);
                    if(headers!=null) {
                        for (String header : headers.keySet()) {
                            String key = header.toString();
                            String value = headers.get(header).toString();
                            httpDelete.setHeader(key, value);
                        }
                    }
                    httpResponse = httpclient.execute(httpDelete);
                    break;
            }
            if(httpResponse!=null) {
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
            }
            else {
                result = "";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    public static String getServerResponse(final Context context, final String serverUrl, final Map<String, String> params) {
        return getServerResponse(context,serverUrl,params,null);
    }
    public static String getServerResponse(final Context context, final String serverUrl, final Map<String, String> params,final Map<String, String> headers) {
        Log.i(TAG, "Sending Post to get response string");

        String result = null;
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to send post");
            try {
//                displayMessage(context, context.getString(
//                        R.string.sending_msg, i, MAX_ATTEMPTS));
                result = stringPost(serverUrl, params,headers);
                String message = "Post Sent on attempt " + i;
                //CommonUtilities.displayMessage(context, message);
                Log.d(TAG, "Post Sent on attempt " + i );
                return result;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to send post on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();

                    return result;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        Log.e(context.toString(), "msg:= " +serverUrl+" : "+(params!=null?params.toString():""));
        //CommonUtilities.displayMessage(context, message);
        return result;
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static String stringPost(String endpoint, Map<String, String> params,Map<String,String> headers)
            throws IOException {
        String result = "";
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        byte[] bytes = null;
        if(params!=null) {
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            // constructs the POST body using the parameters

            while (iterator.hasNext()) {
                Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            Log.v(TAG, "Posting '" + body + "' to " + url);
            bytes = body.getBytes();
        }
        HttpURLConnection conn = null;
        try {
            Log.d("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            if(bytes!=null)
                conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            if(headers!=null){
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            // post the request
            if(bytes!=null) {
                OutputStream out = conn.getOutputStream();

                out.write(bytes);
                out.close();
            }
            // handle the response                       
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            result = sb.toString();
            return result;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        String result = "";
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.d("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response                       
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
