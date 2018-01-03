package com.vrd.tech.yfd.network;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;

import com.vrd.tech.yfd.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadIntentService extends IntentService {

    private static final String ACTION_DOWNLOAD = "com.example.tsang.practice.multithread.action.Download";

    // TODO: Rename parameters
    private static final String DOWNLOAD_URL = "com.example.tsang.practice.multithread.extra.PARAM1";

    private static final String DOWNLOAD_VERSION = "com.example.tsang.practice.multithread.extra.PARAM2";

    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    private Notification notification;
    NotificationCompat.Builder mBuilder;
    NotificationManager manager;
    final int notifyId = 101;
    int currentProgress = 0;
    Timer timer;
    File downloadFile;

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionDownload(Context context, String downloadUrl, String verison) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(DOWNLOAD_URL, downloadUrl);
        intent.putExtra(DOWNLOAD_VERSION, verison);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(DOWNLOAD_URL);
                final String param2 = intent.getStringExtra(DOWNLOAD_VERSION);
                handleActionDownload(param1, param2);
            }
        }
    }


    private void handleActionDownload(String url, String version) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateProgress(currentProgress);
            }
        }, 100, 100);
        createRequest(url, version);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
    }

    private void showNotification() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        notification = mBuilder
                .setContentTitle("正在下载")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(), R.mipmap.ic_launcher)).setColor(Color.parseColor("#E78B7B"))
                .setProgress(100, 0, false)
                .setOngoing(true)
                .build();
        manager.notify(notifyId, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        manager.cancel(notifyId);
        notification = null;
        if (downloadFile != null) {
            if (downloadFile.exists()) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setDataAndType(Uri.parse("file://" + downloadFile.toString()),
                        "application/vnd.android.package-archive");
                getApplicationContext().startActivity(i);
            }
        }
    }

    private void createRequest(String url, String version) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            downloadFile = saveFile(response, version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File saveFile(Response response, String version) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[4096];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;

            File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Download");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "yunfangdao-" + version + ".apk");
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                currentProgress = (int) (finalSum * 1.0f / total * 100);
            }
            fos.flush();
            return file;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }

        }
    }

    void updateProgress(int currentPercent) {
        mBuilder.setProgress(100, currentPercent, false);
        mBuilder.setContentText(currentPercent + "%");
        manager.notify(notifyId, mBuilder.build());
    }
}
