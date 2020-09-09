package com.pachain.android.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import androidx.core.app.ActivityCompat;

public class SDCardUtils {
    private Context context;
    private String SDPATH;

    public SDCardUtils(Context context) {
        this.context = context.getApplicationContext();
        SDPATH = getSDCardPathByEnvironment() + "/";
    }

    public boolean isSDCardEnableByEnvironment() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public String getSDCardPathByEnvironment() {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //path = Environment.getExternalStorageDirectory().getAbsolutePath();
            path = context.getExternalFilesDir(null).getPath();
        }
        return path;
    }

    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public boolean deleteFile(String fileName){
        File file = new File(SDPATH + fileName);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public File createDir(String dirName){
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }

    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    public String readPEMFileFromSD(String path, String fileName) {
        File file = new File(SDPATH + path + fileName);
        if (file.exists()) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(file);
                int len = 0;
                StringBuffer strContent = new StringBuffer("");
                while ((len = fin.read()) != -1) {
                    strContent.append((char) len);
                }
                return strContent.toString().replace("-----BEGIN PRIVATE KEY-----\n", "")
                    .replace("-----END PRIVATE KEY-----", "").replace("\n", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void saveKeyAsPEM(String content, String path, String fileName) throws Exception {
        File file = new File(SDPATH + path + fileName);
        if (file.isFile() && file.exists()) {
            return;
        } else {
            createDir(path);
            file.createNewFile();
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            int i = 0;
            for (; i < (content.length() - (content.length() % 64)); i += 64) {
                randomAccessFile.write(content.substring(i, i + 64).getBytes());
                randomAccessFile.write('\n');
            }
            randomAccessFile.write(content.substring(i, content.length()).getBytes());
            randomAccessFile.write('\n');
            randomAccessFile.write("-----END PRIVATE KEY-----".getBytes());
        }
    }
}
