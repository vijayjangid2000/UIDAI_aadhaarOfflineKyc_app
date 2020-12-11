package com.vijayjangid.aadharkyc;

import android.content.SharedPreferences;
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

import org.w3c.dom.Document;
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

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Verification extends AppCompatActivity {

    // certificatePath and xml path
    final static String certificatePath = "file:///android_asset/uidai_auth_sign_prod_2023.txt";
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

        if (!verifySignature(xmlFilePath,
                certificatePath))
            sb.append("Signature Verification Failed\n");
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

    public boolean verifySignature(String signedXmlFilePath,
                                   String publicKeyFilePath) {
        boolean validFlag = false;
        try {
            File file = new File(signedXmlFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0)
                throw new Exception("No XML Digital Signature Found, document is discarded");
            AssetManager assetManager = getAssets();
            InputStream ims = assetManager.open("uidai_auth_sign_prod_2023.txt");
            //FileInputStream fileInputStream = new FileInputStream(publicKeyFilePath);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(ims);
            PublicKey publicKey = cert.getPublicKey();
            DOMValidateContext valContext = new DOMValidateContext(publicKey, nl.item(0));
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            validFlag = signature.validate(valContext);
        } catch (Exception e) {
            Toast.makeText(this, "Failed signature " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return validFlag;
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
            Toast.makeText(Verification.this, "File not found"
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
}