package com.devops.krakenlabs.filemanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

public class FileManagerService extends Service {
    public static String TAG = FileManagerService.class.getSimpleName();

    public FileManagerService() {
        Log.e(TAG, "FileManagerService: " );
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: intent");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void getDetail() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

//            manager.requestPermission(device, mPermissionIntent);
//            String Model = device.getDeviceName();
//
//            String DeviceID = device.getDeviceId();
//            String Vendor   = device.getVendorId();
//            String Product  = device.getProductId();
//            String Class    = device.getDeviceClass();
//            String Subclass = device.getDeviceSubclass();

        }
    }
}
