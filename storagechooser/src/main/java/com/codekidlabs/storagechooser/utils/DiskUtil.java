package com.codekidlabs.storagechooser.utils;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.fragments.SecondaryChooserFragment;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.models.Storages;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.codekidlabs.storagechooser.Constant.SC_PREFERENCE_KEY;

public class DiskUtil {

    public static final String IN_KB = "KiB";
    public static final String IN_MB = "MiB";
    public static final String IN_GB = "GiB";


    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static void saveChooserPathPreference(SharedPreferences sharedPreferences, String path) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SC_PREFERENCE_KEY, path);
            editor.apply();
        } catch (NullPointerException e) {
            Log.e("StorageChooser", "No sharedPreference was supplied. Supply sharedPreferencesObject via withPreference() or disable saving with actionSave(false)");
        }
    }

    public static boolean isLollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    public static List<Storages> populateStoragesList(Content content) {
        MemoryUtil memoryUtil = new MemoryUtil();
        List<Storages> storagesList = Lists.newArrayList();

        File storageDir = new File("/storage");
        String internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File[] volumeList = storageDir.listFiles();

        Storages storages = new Storages();

        // just add the internal storage and avoid adding emulated henceforth
        storages.setStorageTitle(content.getInternalStorageText());

        storages.setStoragePath(internalStoragePath);
        storages.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(internalStoragePath)));
        storages.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(internalStoragePath)));
        storagesList.add(storages);


        for (File f : volumeList) {
            // Xiaomi Box use external_storage/sda1
            if (f.getName().equals(MemoryUtil.MIBOX_EXTERNAL_STORAGE)) {
                File[] files = f.listFiles();

                boolean hasUsb = false;
                for (File rf : files) {
                    if (rf.getName().indexOf("sda") == 0) {
                        Storages sharedStorage = new Storages();
                        String fPath = rf.getAbsolutePath();
                        sharedStorage.setStorageTitle(content.getUsbStorageText());
                        sharedStorage.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath)));
                        sharedStorage.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath)));
                        sharedStorage.setStoragePath(fPath);
                        storagesList.add(sharedStorage);
                        hasUsb = true;
                    }
                }
                // if no usb defined, use external_storage.
                if (!hasUsb) {
                    Storages sharedStorage = new Storages();
                    String fPath = f.getAbsolutePath();
                    sharedStorage.setStorageTitle(f.getName());
                    sharedStorage.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath)));
                    sharedStorage.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath)));
                    sharedStorage.setStoragePath(fPath);
                    storagesList.add(sharedStorage);
                }
            } else if (!f.getName().equals(MemoryUtil.SELF_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_KNOX)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.SDCARD0_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.CONTAINER)) {
                Storages sharedStorage = new Storages();
                String fPath = f.getAbsolutePath();
                sharedStorage.setStorageTitle(f.getName());
                sharedStorage.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath)));
                sharedStorage.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath)));
                sharedStorage.setStoragePath(fPath);
                storagesList.add(sharedStorage);
            }
        }
        return storagesList;
    }
 }
