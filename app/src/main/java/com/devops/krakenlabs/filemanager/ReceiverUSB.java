package com.devops.krakenlabs.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Alan Giovani Cruz Méndez on 20/11/17 11:36.9
 * cruzmendezalan@gmail.com
 */

public class ReceiverUSB extends BroadcastReceiver {
    private static String TAG = ReceiverUSB.class.getSimpleName();
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.e(TAG, "onReceive: ReceiverUSB" );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showUSB();
            }
        }, 5000);

        sendToast();
        if (intent.getAction().equalsIgnoreCase(
                "android.intent.action.UMS_CONNECTED")) {
            Log.e(TAG, "onReceive: android.intent.action.UMS_CONNECTED" );
        }
    }
    private void sendToast(){
        Toast.makeText(context,"USB conectada",
                Toast.LENGTH_SHORT).show();
    }

    private void showUSB(){
        Log.e(TAG, "showUSB() called");
        File[] externalStorageFiles= ContextCompat.getExternalFilesDirs(context,null);
        for (int i = 0; i <externalStorageFiles.length; i++) {
            Log.e(TAG, "showUSB: "+externalStorageFiles[i].getAbsolutePath() );
        }

        StorageUtils storageUtils = new StorageUtils();
        List<StorageUtils.StorageInfo> storageInfoList = storageUtils.getStorageList();
        for (StorageUtils.StorageInfo sto: storageInfoList
             ) {
            File externalFolder = new File(sto.path);
            if (externalFolder.isDirectory()){
                Log.w(TAG, "Existe y es directorio "+sto.path);
                File[] listOfFiles = externalFolder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        Log.e(TAG,"File " + listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        Log.e(TAG,"Directory " + listOfFiles[i].getName());
                    }
                }
            }
            Log.e(TAG, "showUSB: "+sto.getDisplayName() );
        }
    }


    private boolean canWriteToFlash() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Read only isn't good enough
            return false;
        } else {
            return false;
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {
                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }
}
