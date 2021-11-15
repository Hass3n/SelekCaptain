package minya.salek.salekcaptain;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;



import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import minya.salek.salekcaptain.Model.CaptainModel;

public class Prevalent {

    public static CaptainModel currentOnlineUser;
    public static TripModel currentTripModel;

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }

    public static boolean isValidMobile(String mobile) {
        boolean check;
        if (mobile.length() == 11) {
            if (mobile.startsWith("01")) {
                check = true;
            } else {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }
public static boolean isValidID(String id) {
        boolean check;
        if (id.length() == 14) {
            if (id.startsWith("2")) {
                check = true;
            } else {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static boolean isConnection(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo WiFiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo MobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (WiFiConn != null && WiFiConn.isConnected() || (MobileConn != null && MobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }

    }

    public static void showCustomDialog(Activity activity) {
        SweetAlertDialog connectionDialog = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE);
        connectionDialog.setContentText("من فضلك اتصل بالانترنت");
        connectionDialog.setCancelable(false);
        connectionDialog.setCanceledOnTouchOutside(false);
        connectionDialog.setConfirmText("اتصل");
        connectionDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismiss();
                    activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }).setCancelText("إلغاء")
                .setCancelClickListener(Dialog::dismiss).show();

    }

    public static void showLocationOnMaps(Context context, String location, String name) {
        String uriBegin = "geo:" + location;
        String query = location + "(" + name + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=15";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(Intent.createChooser(intent, name));


    }

}
