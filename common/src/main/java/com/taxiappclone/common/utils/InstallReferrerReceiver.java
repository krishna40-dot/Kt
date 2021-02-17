package com.taxiappclone.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.taxiappclone.common.helper.SessionManager;

public class InstallReferrerReceiver extends BroadcastReceiver {

    public static final String REFERRER = "REF";
    public static final String UTM_CAMPAIGN = "utm_campaign";
    public static final String UTM_SOURCE = "utm_source";
    public static final String UTM_MEDIUM = "utm_medium";
    public static final String UTM_TERM = "utm_term";
    public static final String UTM_CONTENT = "utm_content";

    private final String[] sources = {
            UTM_CAMPAIGN, UTM_SOURCE, UTM_MEDIUM, UTM_TERM, UTM_CONTENT
    };
    private SessionManager session;

    @Override
    public void onReceive(Context context, Intent intent) {

        session = new SessionManager(context);
        if(session.getStringValue("referrer").equals(null)||session.getStringValue("referrer").equals("")) {
            String referrerString = intent.getStringExtra("referrer");
            if (!referrerString.equals(null)) {
                try {
                    Map<String, String> getParams = getHashMapFromQuery(referrerString);
                    session.setValue("referrer", referrerString);
                    for (String sourceType : sources) {
                        String source = getParams.get(sourceType);
                        if (source != null) {
                            session.setValue(sourceType, source);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.e("Referrer Error", e.getMessage());
                } finally {
                    // Pass along to google
                    InstallReferrerReceiver receiver = new InstallReferrerReceiver();
                    receiver.onReceive(context, intent);
                }
            }
        }
    }

    public static Map<String, String> getHashMapFromQuery(String query)
            throws UnsupportedEncodingException {

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

}