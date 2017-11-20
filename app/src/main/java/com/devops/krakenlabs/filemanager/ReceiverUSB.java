package com.devops.krakenlabs.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    public static String PROMOS = "/promos";
    public static String PROMOSMOVIES = "/promosmovies";
    public static String BANNERS = "/banners";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.e(TAG, "onReceive: ReceiverUSB" );
        //We need a delay 5segs because SO is mounting USB
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fileEngine();
            }
        }, 5000);

        sendToast("Inicializando motor de archivos");
    }
    private void sendToast(String msg){
        Toast.makeText(context,msg,
                Toast.LENGTH_SHORT).show();
    }

    private void fileEngine(){
        sendToast("Buscando archivos nuevos");
        File[] externalStorageFiles= ContextCompat.getExternalFilesDirs(context,null);
        for (int i = 0; i <externalStorageFiles.length; i++) {
            Log.e(TAG, "fileEngine: "+externalStorageFiles[i].getAbsolutePath() );
        }

        //Validating if local SDCard is avalilable
        File externalSDCard = new File("/mnt/external_sd");
        if (externalSDCard.exists()){
            sendToast("Se a encontrado SD Externa montada..");
            StorageUtils storageUtils = new StorageUtils();
            List<StorageUtils.StorageInfo> storageInfoList = storageUtils.getStorageList();
            for (StorageUtils.StorageInfo sto: storageInfoList) {
                //Extract only from usb dispositives
                if (sto.path.contains("usb_storage")){
                    File externalUsbPromos = new File(sto.path+PROMOS);
                    displayContentFolder(externalUsbPromos);

                    File externalUsbPromosMovies = new File(sto.path+PROMOSMOVIES);
                    displayContentFolder(externalUsbPromosMovies);

                    File externalUsbBanners = new File(sto.path+BANNERS);
                    displayContentFolder(externalUsbBanners);
                    try {
                        sendToast("Copiando promos..");
                        copyDirectoryOneLocationToAnotherLocation(externalUsbPromos,new File("/mnt/external_sd"+PROMOS));
                        sendToast("Copiando Movies..");
                        copyDirectoryOneLocationToAnotherLocation(externalUsbPromosMovies,new File("/mnt/external_sd"+PROMOSMOVIES));
                        sendToast("Copiando Banners..");
                        copyDirectoryOneLocationToAnotherLocation(externalUsbBanners,new File("/mnt/external_sd"+BANNERS));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.w(TAG, "Existe y es directorio "+sto.path);
                }
            }
        }
        sendToast("Proceso completado, usted puede desmontar la USB");
    }

    /**
     * If u need see the content of folder, call this method
     * @param externalFolder
     */
    private void displayContentFolder(File externalFolder){
        if (externalFolder.exists()&&externalFolder.isDirectory()){
            File[] listOfFiles = externalFolder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    Log.e(TAG,"File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    Log.e(TAG,"Directory " + listOfFiles[i].getName());
                }
            }
        }
    }

    /**
     * Internal SDcard is available to write
     * @return
     */
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

    /**
     * Copy only one file
     * @param inputPath
     * @param inputFile
     * @param outputPath
     */
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

    /**
     * Move all content from sourcelocation to targetlocation
     * @param sourceLocation
     * @param targetLocation
     * @throws IOException
     */
    public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation) throws IOException {
        Log.d(TAG, "copyDirectoryOneLocationToAnotherLocation() called with: sourceLocation = [" + sourceLocation + "], targetLocation = [" + targetLocation + "]");
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length   ; i++) {
                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            if (sourceLocation.exists()){
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
            }else{
                Log.e(TAG, "copyDirectoryOneLocationToAnotherLocation: " );
            }
        }

    }
}
