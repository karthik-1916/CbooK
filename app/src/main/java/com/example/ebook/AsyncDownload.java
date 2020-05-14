package com.example.ebook;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

import static android.content.Context.DOWNLOAD_SERVICE;

public class AsyncDownload extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "CARLZ";
    private String fileName;
    private String fileExtension;
    private String URL;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    public AsyncDownload(String fileName,String fileExtension,String URL,Context context) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.URL = URL;
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        String filepath = Environment.getExternalStorageDirectory() + File.separator + "CbooK";
        File folder = new File(filepath);

        if (!folder.exists()) {
            folder.mkdir();
            Log.i(TAG, "Creating Folder " + folder);
        }
        try {
            Log.i(TAG, "Download Started");
            Uri downloadUri = Uri.parse(URL);

            Log.d(TAG,"Requesting download");
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            Log.d(TAG,"Setting allowed network type to WIFI only");
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.allowScanningByMediaScanner();

            Log.d(TAG,"Setting destination folder");
            request.setDestinationInExternalPublicDir("/CbooK", fileName + "." + fileExtension);

            Log.d(TAG,"Working on notification");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
            long id = downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Download failed for some reason");
            e.printStackTrace();
        }
        return null;
    }

}
