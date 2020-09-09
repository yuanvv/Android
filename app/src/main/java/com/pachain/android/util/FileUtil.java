package com.pachain.android.util;

import android.content.Context;
import android.os.Environment;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    private String SDPATH;
    private Context context;

    public FileUtil(Context context) {
        this.context = context.getApplicationContext();
        SDPATH = getDiskCacheDir() + "/";
    }

    public String getDiskCacheDir() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public String getSDPATH() {
        return SDPATH;
    }

    public String getFilePath(String path, String fileName) {
        return SDPATH + path + fileName;
    }

    public String getFilePath(String filePath) {
        return SDPATH + filePath;
    }

    public FileUtil(String SDPATH){
        SDPATH = Environment.getExternalStorageDirectory() + "/" ;
    }

    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    public File createDir(String dirName){
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
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

    public File write2SDFromInput(String path, String fileName, InputStream input){
        File file = null;
        OutputStream output = null;
        try {
            byte[] arr = new byte[1];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(baos);
            int n = input.read(arr);
            while (n > 0) {
                bos.write(arr);
                n = input.read(arr);
            }
            bos.close();

            createDir(path);
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);
            output.write(baos.toByteArray());
            output.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void writeImageFromByte(String path, String fileName, byte[] byteArray) {
        if (byteArray.length > 0) {
            File photoFile = null;
            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bStream = null;
            try {
                createDir(path);
                photoFile = createSDFile(path + fileName);
                fileOutputStream = new FileOutputStream(photoFile);
                bStream = new BufferedOutputStream(fileOutputStream);
                bStream.write(byteArray);
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    bStream.close();
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public File readFileFromSD(String path, String fileName) {
        File file = new File(SDPATH + path + fileName);
        return file;
    }

    public File createDirAndFile(String path, String fileName) {
        File photoFile = null;
        try {
            createDir(path);
            deleteFile(path + fileName);
            photoFile = createSDFile(path + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoFile;
    }
}
