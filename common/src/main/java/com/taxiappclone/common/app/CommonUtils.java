package com.taxiappclone.common.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.taxiappclone.common.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonUtils {
    private static AlertDialog.Builder builder;
    private static AlertDialog alertDialog;
    Context context;
    static Context mContext;

    public CommonUtils(Context context) {
        this.context = context;
        mContext = context;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_theme_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    public static double getDistanceByLatLng(LatLng latLng1, LatLng latLng2, char unit) {
        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public static String getDateObject(String strDate) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("US/Central"));

        if (date != null)
            return simpleDateFormat.format(date);
        return "";
    }

    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date stringToDateTime(String dateStr) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToDate(String dateStr) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String convertDateFormat(String dateString, String oldPattern,
                                           String newPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(oldPattern);
        Date testDate = null;
        try {
            testDate = sdf.parse(dateString);
            SimpleDateFormat formatter = new SimpleDateFormat(newPattern);
//      formatter.setTimeZone(TimeZone.getTimeZone("US/Central"));
            dateString = formatter.format(testDate);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String getDateString() {
        String dateStr = "";
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = dateFormat.format(date);
        return dateStr;
    }

    public static String getDateTimeString() {
        String dateStr = "";
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateStr = dateFormat.format(date);
        return dateStr;
    }

    public static String convertToHumanTime(int time) {
        String t = "";

        int d = time / (24 * 60);
        int h = (time % (24 * 60)) / 60;
        int m = time;

        if (d > 0)
            t += d + "day(s),";
        if (h > 0)
            t += h + "hr ";
        t += m + "min";
        return t;

    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static String getCreditCardType(String credCardNumber) {
        String creditCard = credCardNumber.trim();
        //start without knowing the credit card type
        String cardType = "";
        if (creditCard.length() >= 13 && creditCard.length() <= 16
                && creditCard.startsWith("4")) {
            cardType = "Visa";
        } else if (creditCard.length() == 16) {
            int prefix = Integer.parseInt(creditCard.substring(0, 2));
            if (prefix >= 51 && prefix <= 55) {
                cardType = "MasterCard";
            }
        } else if (creditCard.length() == 15
                && (creditCard.startsWith("34") || creditCard
                .startsWith("37"))) {
            cardType = "Amex";
        } else if (creditCard.length() == 16 && creditCard.startsWith("6011")) {
            cardType = "Discover";
        } else
            cardType = "RuPay";

  /*//first check for MasterCard
  if (accountNumber.matches("^(5[0-5])"))
  {
    result = "mastercard";
  }
  //then check for Visa
  else if (accountNumber.matches("^4"))
  {
    result = "Visa";
  }
  //then check for AmEx
  else if (accountNumber.matches("^(3[4,7])"))
  {
    result = "amex";
  }*/

        return cardType;
    }

    public static void shareApp(Context context, String referrer) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey friends download & install this App with referral code " + referrer + " & Get up to 100% cashback on Recharge, Bill payment,  Mobile & Bike Repair services." +
                        "\nhttp://shricard.com/download_app.php?referrer=" + referrer);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static void showAlert(Activity context, String msg) {
        showAlert(context, 0, "", msg);
    }

    public static void showAlert(Activity context, int status, String title, String msg) {
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
        }
        // Setting Dialog Title
        builder.setTitle(title);

        // Setting Dialog Message
        builder.setMessage(msg);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        // Showing Alert Message
    /*if(!context.isFinishing())
      alertDialog.show();*/
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void copyText(Context context, String text) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("text to clip");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    public static String getNameLetters(String str) {
        String sName = "";
        try {
            if (str.contains(" ")) {
                String[] name = str.trim().split("\\s+");
                for (int i = 0; i < name.length; i++) {
                    if (i == 2)
                        break;
                    sName = sName + name[i].substring(0, 1);
                }
            } else {
                sName = str.substring(0, 1);
            }
        } catch (StringIndexOutOfBoundsException exp) {
            exp.printStackTrace();
        }
        return sName;
    }

    public static String numberWithCommas(double paramDouble) {
        String str;
        if (String.valueOf(paramDouble).contains(".")) {
            DecimalFormat localDecimalFormat = new DecimalFormat("##,##,##,##,##,##,##0.##");
            str = localDecimalFormat.format(paramDouble);
        } else {
            DecimalFormat localDecimalFormat = new DecimalFormat("#,###,###");
            str = localDecimalFormat.format(paramDouble);
        }
        return str;
    }

    public static String roundDouble(double paramDouble) {
        @SuppressLint("DefaultLocale") String str1 = String.format("%.2f", paramDouble);


        return str1;
    }

    public static String getUfMobNo(String phone) {
        //String number = "+91(999)338-3667";
        String tel = phone.replaceAll("\\D", "");
        int telLength = tel.length();
        if (telLength > 9)
            tel = tel.substring(telLength - 10, telLength);
        return tel;
    }

    public static String convertAmount(double amount) {
        try {
            DecimalFormat format = new DecimalFormat();
            format.setDecimalSeparatorAlwaysShown(false);
            return format.format(amount);
        } catch (ArithmeticException exp) {
            exp.printStackTrace();
        }
        return "";
    }

    public static String trimString(String str, int length) {
        if (str.length() > length)
            str = str.substring(0, length) + "...";
        return str;
    }

    public static void hideNavigationBar(Activity activity) {
        int currentApiVersion = Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = activity.getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getContactDisplayNameByNumber(Context context, String number) {
        number = getUfMobNo(number);
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = number;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        return name;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String bitmapToString(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public String getContactName(String number) {
    /*try
    {
      String C_Num = getUfMobNo(number);
      String HAS_PHONE_NUMBER= ContactsContract.Contacts.HAS_PHONE_NUMBER;
      Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null,null);
      if (phones.getCount() > 0)
      {
        while (phones.moveToNext())
        {
          //	get name and number from cursor using column index
          String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          int num = Integer.parseInt(phones.getString(phones.getColumnIndex(HAS_PHONE_NUMBER)));


          if(C_Num.equals(this.getUfMobNo(phoneNumber)))
          {
            if(C_Num.equals(""))
              return number;
            return name;
          }
        }
      }
      else
      {
        return number;
      }
      phones.close();
    }
    catch(Exception e)
    {
      //tv.append("Some error occured");
    }*/
        return number;
    }

  /*public Boolean fetchAllContacts(){
    try
    {
      db = new SQLiteHandler(context);
      db.deleteAllContacts();
      int count = 0;
      Toast.makeText(context,"Fetching Contacts...",Toast.LENGTH_SHORT).show();
      String HAS_PHONE_NUMBER= ContactsContract.Contacts.HAS_PHONE_NUMBER;
      Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null,null);
      if (phones.getCount() > 0)
      {
        while (phones.moveToNext())
        {
          String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          String phoneNumber = getUfMobNo(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
          if(!phoneNumber.trim().equals(""))
          {
            ContentValues content = new ContentValues();
            content.put("name",name);
            content.put("number",phoneNumber);
            if(!db.checkContactExists(phoneNumber))
            {
              count++;
              db.addContact(content);
            }
          }
        }

        Toast.makeText(context,count +"contact added",Toast.LENGTH_SHORT).show();
      }
      else
      {
        return true;
      }
      phones.close();
    }
    catch(Exception e)
    {
      //tv.append("Some error occured");
    }
    return true;
  }*/
}
