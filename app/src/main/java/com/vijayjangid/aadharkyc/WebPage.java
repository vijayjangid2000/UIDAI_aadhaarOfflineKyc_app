package com.vijayjangid.aadharkyc;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.util.Date;

public class WebPage extends AppCompatActivity {

    WebView webView;
    final static String uidai = "https://resident.uidai.gov.in/offline-kyc";
    String aadhaarNumber, shareCode, otpNumber, captcha;
    EditText aadharNumberTv, shareCodeTv, otpTv, captchaTv;
    ImageView captchaIv;
    Button getOtpButton, verifyBtn;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        // setting id's here
        webView = findViewById(R.id.webView);
        aadharNumberTv = findViewById(R.id.aadharTv);
        captchaTv = findViewById(R.id.captchaTv);
        captchaIv = findViewById(R.id.captchaIv);
        shareCodeTv = findViewById(R.id.sharecodeTv);
        otpTv = findViewById(R.id.otpTv);
        getOtpButton = findViewById(R.id.getotpButton);
        verifyBtn = findViewById(R.id.downloadButton);

        // filling editText if old data exists
        sp = getSharedPreferences("User", MODE_PRIVATE);
        aadhaarNumber = sp.getString("aadhaarNumber", "");
        shareCode = sp.getString("shareCode", "");
        if (!aadhaarNumber.equals("")) aadharNumberTv.setText(aadhaarNumber);
        if (!shareCode.equals("")) shareCodeTv.setText(shareCode);

        // click listeners here
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOtp();
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setOtp();
                startActivity(new Intent(WebPage.this, Verification.class));
            }
        });

        // opening link
        webViewWork();
    }

    boolean second = false;

    /*setting webView */
    public void webViewWork() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(Environment.getExternalStorageDirectory().getAbsolutePath()
                + Environment.DIRECTORY_DOWNLOADS);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Aadhar file");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

                Toast.makeText(getApplicationContext(),
                        "Downloading " + URLUtil.guessFileName(url, contentDisposition, mimeType),
                        Toast.LENGTH_LONG).show();
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                String imageUrl = "https://resident.uidai.gov.in/CaptchaSecurityImages.php?width=100&height=40&characters=5";
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round);
                Glide.with(WebPage.this).load(imageUrl).apply(options).into(captchaIv);
            }
        });

        webView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
        webView.loadUrl(uidai);

    }



    /*here is the code I tried to download the zip file
    * I think the file is generated at runtime in javascript
    * and I found that this is called blob file
    * I copied this javascript interface from stackoverflow
    * but the problem is that sendData method is not even called,
    * Here I am confused about that. May be it is not a blob file.*/
    public static class MyJavascriptInterface {
        Context context;

        MyJavascriptInterface(Context c) {
            Toast.makeText(c, "Context created", Toast.LENGTH_SHORT).show();
            context = c;
        }

        @JavascriptInterface
        public void sendData(String data) {
            Toast.makeText(context, "Data generated size = " + data.length() / 8
                    , Toast.LENGTH_SHORT).show();
            Log.d("tiger", "sendData: " + data);
        }

        public static String getBase64StringFromBlobUrl(String blobUrl) {
            if (blobUrl.startsWith("blob")) {
                return "javascript: var xhr = new XMLHttpRequest();" +
                        "xhr.open('GET', '" + blobUrl + "', true);" +
                        "xhr.setRequestHeader('Content-type','application/zip');" +
                        "xhr.responseType = 'blob';" +
                        "xhr.onload = function(e) {" +
                        "    if (this.status == 200) {" +
                        "        var blobPdf = this.response;" +
                        "        var reader = new FileReader();" +
                        "        reader.readAsDataURL(blobPdf);" +
                        "        reader.onloadend = function() {" +
                        "            base64data = reader.result;" +
                        "            Android.getBase64FromBlobData(base64data);" +
                        "        }" +
                        "    }" +
                        "};" +
                        "xhr.send();";
            }
            return "javascript: console.log('It is not a Blob URL');";
        }

        private void convertBase64StringToPdfAndStoreIt(String base64PDf) throws IOException {
            final int notificationId = 1;
            String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
            final File dwldsPath = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + "/YourFileName_" + currentDateTime + "_.pdf");
            byte[] pdfAsBytes = Base64.decode(base64PDf.replaceFirst("^data:application/pdf;base64,", ""), 0);
            FileOutputStream os;
            os = new FileOutputStream(dwldsPath, false);
            os.write(pdfAsBytes);
            os.flush();

            if (dwldsPath.exists()) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri apkURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", dwldsPath);
                intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                String CHANNEL_ID = "MYCHANNEL";
                final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_LOW);
                    Notification notification = new Notification.Builder(context, CHANNEL_ID)
                            .setContentText("You have got something new!")
                            .setContentTitle("File downloaded")
                            .setContentIntent(pendingIntent)
                            .setChannelId(CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.sym_action_chat)
                            .build();
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(notificationChannel);
                        notificationManager.notify(notificationId, notification);
                    }

                } else {
                    NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(android.R.drawable.sym_action_chat)
                            //.setContentIntent(pendingIntent)
                            .setContentTitle("MY TITLE")
                            .setContentText("MY TEXT CONTENT");

                    if (notificationManager != null) {
                        notificationManager.notify(notificationId, b.build());
                        Handler h = new Handler();
                        long delayInMilliseconds = 1000;
                        h.postDelayed(new Runnable() {
                            public void run() {
                                notificationManager.cancel(notificationId);
                            }
                        }, delayInMilliseconds);
                    }
                }
            }
            Toast.makeText(context, "PDF FILE DOWNLOADED!", Toast.LENGTH_SHORT).show();
        }
    }

    /*here we will receive otp by filling form*/
    public void getOtp() {

        aadhaarNumber = aadharNumberTv.getText().toString().trim();
        captcha = captchaTv.getText().toString().trim();

        sp.edit().putString("aadhaarNumber", aadhaarNumber).apply();

        webView.loadUrl("javascript: {" +
                "var x = document.getElementById('uid1').value = '" + aadhaarNumber + "';" +
                "x = document.getElementById('security_code').value = '" + captcha + "';" +
                "x = document.getElementById('smt_btn').click(); };");

    }

    /*here we will type otp and start downloading file*/
    public void setOtp() {
        shareCode = shareCodeTv.getText().toString().trim();
        otpNumber = otpTv.getText().toString().trim();
        sp.edit().putString("shareCode", shareCode).apply();

        webView.loadUrl("javascript: {" +
                "var x = document.getElementsByName('zipcode')[0].value = '" + shareCode + "'; " +
                "x = document.getElementsByName('totp')[0].value = '" + otpNumber + "'; " +
                "x = document.getElementById('smt_btn').click(); };");

        webView.loadUrl("javascript: {" +
                "var x = document.getElementsByName('zipcode')[0].value = '" + shareCode + "'; " +
                "x = document.getElementsByName('totp')[0].value = '" + otpNumber + "'; " +
                "var frms = document.getElementsByName('adminForm');" +
                "frms[0].submit(); };");


    }

    /*file downloads here, zip file created after that moves to next activity (Verification)*/
    public void downloadFile(URL url, String outputFileName) throws IOException {

        try (InputStream in = url.openStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

        Toast.makeText(this, "Called " + url, Toast.LENGTH_SHORT).show();
    }
}