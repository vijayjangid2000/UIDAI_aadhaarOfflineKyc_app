package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.lingala.zip4j.ZipFile;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;

public class Verification_activity extends AppCompatActivity {

    // certificatePath and xml path
    final static String certificatePath = "file:///android_asset/uidai_auth_sign_prod_2023.cer";
    String xmlFilePath;

    // data given by user while registering
    String name, mobile, email, father, gender, birthDate, shareCode;

    // all string from containing xml data are ended with x
    String namex, birthDatex, emailx, genderx, mobilex, careofx,
            countryx, districtx, housex, landmarkx, locationx,
            pincodex, pox, statex, streetx, subdistx, vtcx, photoBytex,
            referenceIdx, tempForXml;

    ImageView userPicIv;
    TextView statusTv;
    Button verifyAgainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        statusTv = findViewById(R.id.statusTv);
        userPicIv = findViewById(R.id.userIv);
        verifyAgainBtn = findViewById(R.id.verifyAgainBtn);

        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        name = sp.getString("name", "");
        mobile = sp.getString("mobile", "");
        email = sp.getString("email", "");
        father = sp.getString("father", "");
        gender = sp.getString("gender", "");
        birthDate = sp.getString("birthDate", "");
        shareCode = sp.getString("shareCode", "2222");

        verifyAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getZipFile();
            }
        });
        // here we initialize the process
        getZipFile();
    }

    void completeVerification() {

        StringBuilder sb = new StringBuilder("");

        if (!name.equalsIgnoreCase(namex)) sb.append(name + ", " + namex + "\n");
        else sb.append("name verified\n");

        if (!verifySignature())
            sb.append("Signature Verification_activity Failed\n");
        else sb.append("signature passed\n");

        if (!validate(mobile + shareCode,
                4, mobilex))
            sb.append("Incorrect Mobile Number - " + mobile + shareCode + " = " + mobilex + "\n");
        else sb.append("mobile number verified\n");

        if (emailx.equals("")) sb.append("Email not registered\n");
        else if (!validate(email + shareCode,
                Integer.parseInt(String.valueOf(referenceIdx.charAt(3))), emailx))
            sb.append("Incorrect email\n");
        else sb.append("email verified\n");

        if (!birthDate.equalsIgnoreCase(birthDatex))
            sb.append(birthDate + ", " + birthDatex + "\n");
        else sb.append("birthdate verified\n");

        if (!gender.equalsIgnoreCase(genderx)) sb.append(gender + ", " + genderx + "\n");
        else sb.append("gender verified\n");

        if (!("s/o " + father).equalsIgnoreCase(careofx)) sb.append(father + ", " + careofx + "\n");
        else sb.append("father name verified\n");
        // setting image in image view from xml
        byte[] decodedString = Base64.decode(photoBytex, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        userPicIv.setImageBitmap(decodedByte);

        statusTv.setText(sb.toString());
    }

    /*Here we will verify the signature
     * by decrypting their code, and then getting hash
     * by calculating hash from xml then matching them*/
    boolean verifySignature() {
        boolean valid = false;
        try {

            File file = new File(xmlFilePath);
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            Document doc = f.newDocumentBuilder().parse(file);

            NodeList nodes = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature");
            if (nodes.getLength() == 0) {
                throw new Exception("Signature NOT found!");
            }

            Element sigElement = (Element) nodes.item(0);
            XMLSignature signature = new XMLSignature(sigElement, "");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager assetManager = getAssets();
            InputStream ims = assetManager.open("uidai_auth_sign_prod_2023.cer");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(ims);

            if (cert == null) {
                PublicKey pk = signature.getKeyInfo().getPublicKey();
                if (pk == null) {
                    throw new Exception("Did not find Certificate or Public Key");
                }
                valid = signature.checkSignatureValue(pk);
            } else {
                valid = signature.checkSignatureValue(cert);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed signature " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    // This is important!
    static {
        org.apache.xml.security.Init.init();
    }

    /*find the latest downloaded file the call unzip*/
    void getZipFile() {

        File file = new File(Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_DOWNLOADS);
        File[] array = file.listFiles();
        String old = "";
        boolean found = false;

        for (File value : array) {
            if (value.getName().contains("offlineaadhaar")
                    && value.getName().contains(".zip") && old.compareTo(value.getName()) < 0) {
                found = true;
                old = value.getName();
                file = new File(value.getAbsoluteFile().getAbsolutePath());
            }
        }

        if (!found) {
            Toast.makeText(Verification_activity.this, "File not found"
                    , Toast.LENGTH_SHORT).show();
        } else unzip(file, shareCode); // giving address (using File Object) of zip file

    }

    /*unzip file using shareCode, then extract then send file address*/
    void unzip(File file, String password) {

        /* firstly check if the file is extracted or not */
        if (new File(file.getParent() + "/" + file.getName().split("\\.")[0]).exists()) {
            try {
                // if yes then no need to extract, directly show data
                getXmlData(file.getParent() + "/" + file.getName().split("\\.")[0]);
            } catch (Exception e) {
                statusTv.setText("Problem in unzipping file " + e.getMessage() + ". " + file.getAbsolutePath() +
                        " . ");
                e.printStackTrace();
            }
            return;
        }

        // if not extracted then unzipping here
        ZipFile zipFile = new ZipFile(file.getAbsolutePath(),
                password.toCharArray());
        try {
            zipFile.extractAll(file.getParent() + "/" + file.getName().split("\\.")[0]);
            getXmlData(file.getParent() + "/" + file.getName().split("\\.")[0]);
        } catch (Exception e) {
            statusTv.setText("File was deleted or renamed or corrupted." +
                    " Exception - " + e.getMessage());
        }
    }

    /*using file address open xml and then get data into strings (declared above)*/
    void getXmlData(String address) throws XmlPullParserException, IOException {

        File file = new File(address);
        // getting file from the extracted folder
        file = new File(file.listFiles()[0].getAbsolutePath());
        if (!file.getName().contains(".xml")) {
            Toast.makeText(this, "File is invalid, Please download again"
                    , Toast.LENGTH_SHORT).show();
            return;
        } else xmlFilePath = file.getAbsolutePath();

        // parsing the xml file
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser parser = xmlFactoryObject.newPullParser();
        parser.setInput(new FileInputStream(file.getAbsolutePath()), null); //setting input
        //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (event) {

                case XmlPullParser.START_TAG:

                    switch (tagName) {
                        case "OfflinePaperlessKyc":
                            referenceIdx = parser.getAttributeValue(null, "referenceId");
                            break;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    switch (tagName) {
                        case "Poi":
                            namex = parser.getAttributeValue(null, "name");
                            birthDatex = parser.getAttributeValue(null, "dob");
                            emailx = parser.getAttributeValue(null, "e");
                            genderx = parser.getAttributeValue(null, "gender");
                            mobilex = parser.getAttributeValue(null, "m");
                            break;
                        case "Poa":
                            careofx = parser.getAttributeValue(null, "careof");
                            countryx = parser.getAttributeValue(null, "country");
                            districtx = parser.getAttributeValue(null, "dist");
                            housex = parser.getAttributeValue(null, "house");
                            landmarkx = parser.getAttributeValue(null, "landmark");
                            locationx = parser.getAttributeValue(null, "loc");
                            pincodex = parser.getAttributeValue(null, "pc");
                            pox = parser.getAttributeValue(null, "po");
                            statex = parser.getAttributeValue(null, "state");
                            streetx = parser.getAttributeValue(null, "street");
                            subdistx = parser.getAttributeValue(null, "subdist");
                            vtcx = parser.getAttributeValue(null, "vtc");
                            break;
                        case "Pht":
                            photoBytex = tempForXml;
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                    tempForXml = parser.getText();
                    break;
            }
            event = parser.next();
        }

        statusTv.setText(namex + ", " + careofx + ", " + referenceIdx + ", " +
                photoBytex);
        completeVerification();
    }

    /*this calculate hash of the string and match it with SHA256 given in xml*/
    public byte[] getSHA(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 12) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public boolean validate(String mobileEmail, int lastDigit, String SHA256) {
        if (lastDigit == 0) lastDigit++; // same case for 0 and 1

        String createdHashCode = toHexString(getSHA(mobileEmail));

        // now we are getting the hashcode for the string here
        for (int i = 1; i < lastDigit; i++) {
            createdHashCode = toHexString(getSHA(createdHashCode));
        }

        return createdHashCode.equals(SHA256);
    }

    void browseFiles() {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Please Allow to use storage"
                    , Toast.LENGTH_LONG).show();
            askStoragePermission();
            return;
        }

        Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
        browseIntent.setType("application/zip");
        //browseIntent.setType("*/*");
        startActivityForResult(browseIntent, 10);
    }

    public void askStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);
        }
    }

    /*void onFileSelected(int requestCode, int resultCode, @Nullable Intent data) {
        String path = getPath(data.getData());
        String testingPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_DOWNLOADS + "/" + "offlineaadhaar20210123013523099.zip";
        //tv_status.setText(testingPath);
        File file = new File(path);
        zipFileObject = file;

        if (!file.getName().contains(".zip")) {
            tv_status.setText("Please select valid document file for verification " + file.getAbsolutePath());
            tv_status.setTextColor(Color.RED);
            btn_verifyNow.setEnabled(true);
        } else {
            tv_status.setText("Document Found:\n" + file.getAbsolutePath());
            tv_status.setTextColor(Color.parseColor("#487E0A"));
            btn_verifyNow.setEnabled(true);
        }

        ll_afterBrowse.setVisibility(View.VISIBLE);
    }*/
}