package com.pachain.android.common;

import android.os.AsyncTask;
import android.os.Message;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PostApi {
    private final static int TIME_OUT = 30 * 1000;
    private final static int API_OK = 0;
    private final static int API_NO = 1;
    private onApiListener listener;
    private List<String> inParams;
    private String strUrl;
    private AsyncTask<String, Void, Message> asyncTask;

    public PostApi(String inUrl, List<String> inParams) {
        this.inParams = inParams;
        this.strUrl = inUrl;
    }

    public void setOnApiListener(onApiListener listener) {
        this.listener = listener;
    }

    public void call() {
        asyncTask = new AsyncTask<String, Void, Message>() {
            @Override
            protected Message doInBackground(String... params) {
                long begin = System.currentTimeMillis();
                HttpURLConnection conn = null;
                InputStreamReader reader = null;
                StringBuffer buffer = new StringBuffer();
                String postParams = "";
                Message msg = new Message();
                try {
                    URL url = new URL(strUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setReadTimeout(TIME_OUT);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    conn.setRequestProperty("User-agent","com.pachain.android");
                    OutputStream out = conn.getOutputStream();
                    if (inParams != null && inParams.size() > 0) {
                        for (String str : inParams) {
                            out.write(str.getBytes("UTF-8"));
                            postParams += str;
                        }
                    }
                    out.close();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        String contentType = conn.getHeaderField("Content-Type");
                        String encoding = "utf-8";
                        reader = new InputStreamReader(conn.getInputStream(), encoding);
                        char[] charArr = new char[1024 * 8];
                        int len = 0;
                        while ((len = reader.read(charArr)) != -1) {
                            String str = new String(charArr, 0, len);
                            buffer.append(str);
                        }

                        if (listener != null) {
                            try {
                                listener.onExecute(buffer.toString());
                                msg.obj = buffer.toString();
                                msg.what = API_OK;
                            } catch (Exception ex) {
                                msg.obj = ex.getMessage();
                                msg.what = API_NO;
                            }
                        }
                    }
                    else {
                        msg.what = API_NO;
                        msg.obj = "Failed to connect to server. Try again later.";
                    }
                } catch (Exception ex) {
                    msg.what = API_NO;
                    msg.obj = "Failed to connect to server. Try again later.";
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Message result) {
                switch (result.what) {
                    case API_OK:
                        if (listener != null) listener.onSuccessed(result.obj.toString());
                        break;
                    case API_NO:
                        if (listener != null) listener.onFailed(result.obj != null ? result.obj.toString() : "");
                        break;
                    default:
                        break;
                }
            }
        }.execute();
    }

    public interface onApiListener {
        public void onExecute(String content) throws Exception;
        public void onSuccessed(String successed);
        public void onFailed(String error);
    }

    public void destroyAsyncTask() {
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }
        asyncTask = null;
    }
}
