package com.vijayjangid.aadharkyc;

import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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
                setOtp();
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

        webView.setWebViewClient(new WebViewClient(){
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

        webView.loadUrl(uidai);

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

    }

    /*file downloads here, zip file created after that moves to next activity (Verification)*/
    public void downloadFile(URL url, String outputFileName) throws IOException {

        try (InputStream in = url.openStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}