package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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

public class Kyc extends AppCompatActivity {

    final static String link_uidai = "https://resident.uidai.gov.in/offline-kyc";
    // all string from containing xml data are ended with x
    public static String xml_name, xml_birthDate, xml_emailSha, xml_gender, xml_mobileSha, xml_careOf,
            xml_country, xml_district, xml_house, xml_landmark, xml_location,
            xml_pinCode, xml_po, xml_state, xml_street, xml_subDistrict, xml_vtc, xml_photoByteCode,
            xml_referenceId, tempForXml;

    // This is important!
    static {
        org.apache.xml.security.Init.init();
    }

    File fileObj_certificate, fileObj_zipFile;
    Button btn_verifyNow, btn_downloadAadhar;
    TextView tv_browseFile, tv_status;
    EditText et_shareCode;
    LinearLayout ll_afterBrowse;

    public static void recursiveDelete(File file) {
        try {
            //to end the recursive loop
            if (!file.exists())
                return;

            //if directory, go inside and call recursively
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    //call recursively
                    recursiveDelete(f);
                }
            }
            //call delete to delete files and empty directory
            file.delete();
            System.out.println("Deleted file/folder: " + file.getAbsolutePath());
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Upgrade Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        idAndListeners();
        ll_afterBrowse.setVisibility(View.GONE);
    }

    void idAndListeners() {

        btn_downloadAadhar = findViewById(R.id.btn_downloadAadhar);
        tv_browseFile = findViewById(R.id.tv_browseFile);
        tv_status = findViewById(R.id.tv_status);
        et_shareCode = findViewById(R.id.et_shareCode);
        btn_verifyNow = findViewById(R.id.btn_verifyNow);
        ll_afterBrowse = findViewById(R.id.ll_afterBrowse);

        btn_downloadAadhar.setOnClickListener(v -> {
            CustomTabsIntent anotherCustomTab = new CustomTabsIntent.Builder().build();
            anotherCustomTab.launchUrl(Kyc.this, Uri.parse(link_uidai));
        });

        tv_browseFile.setOnClickListener(v -> getZipFile());

        btn_verifyNow.setOnClickListener(v -> startFinalVerification());
    }

    void getZipFile() {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(Kyc.this, "Please Allow to use storage"
                    , Toast.LENGTH_LONG).show();
            askStoragePermission();
            return;
        }

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

        if (found) {
            fileObj_zipFile = file;
            tv_status.setText("Document Found:\n" + file.getAbsolutePath());
            tv_status.setTextColor(Color.parseColor("#487E0A"));
        } else {
            tv_status.setText("Please download the file");
            tv_status.setTextColor(Color.RED);
        }

        ll_afterBrowse.setVisibility(View.VISIBLE);
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    void startFinalVerification() {
        String shareCode = et_shareCode.getText().toString().trim();

        if (shareCode.length() != 4) {
            tv_status.setText("Please enter 4 digit Share Code");
        } else {
            unzip(fileObj_zipFile, shareCode);
        }
    }

    void afterVerificationSuccess() {
        SheetClass sheetClass = new SheetClass();
        sheetClass.show(getSupportFragmentManager(), "exampleBottomSheet");
    }

    /*unzip file using shareCode, then extract then send file address*/
    void unzip(File zipFilePath, String password) {

        String unzipErrorStatement = "Unable to unzip file\nPlease enter correct ShareCode \nor\n choose another File";
        String signatureFailStatement = "Signature Verification Failed\nPlease select valid document";

        /* firstly check if the file is extracted or not
         * if extracted then we are deleting the complete folder
         * and extract it again for verification, reason - there may be extra data there  */
        try {
            File folderUnzipped = new File(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
            if (folderUnzipped.exists()) recursiveDelete(folderUnzipped);
        } catch (Exception ignored) {
        }

        // if not extracted then unzipping here
        ZipFile zipFile = new ZipFile(zipFilePath.getAbsolutePath(),
                password.toCharArray());
        try {
            zipFile.extractAll(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
            File unzipFolder = new File(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
            File[] filesInsideFolder = unzipFolder.listFiles();
            if (filesInsideFolder == null) {
                tv_status.setText("Selected Document is Invalid");
                return;
            }
            if (filesInsideFolder.length != 0) fileObj_certificate = filesInsideFolder[0];
            tv_status.setText("Successfully unzipped");
        } catch (Exception e) {
            tv_status.setText(unzipErrorStatement);
            return;
        }

        if (!verifySignature(fileObj_certificate)) {
            tv_status.setText(signatureFailStatement);
            return;
        } else {
            tv_status.setText("Signature Verified");
        }

        try {
            getXmlData(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
        } catch (Exception e) {
            tv_status.setText("There is some problem right now, try again later");
        }

        afterVerificationSuccess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /*Here we will verify the signature
     * by decrypting their code, and then getting hash
     * by calculating hash from xml then matching them*/
    boolean verifySignature(File xmlFile) {
        boolean valid = false;
        try {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            Document doc = f.newDocumentBuilder().parse(xmlFile);

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
        }

        return valid;
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
        }

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
                            xml_referenceId = parser.getAttributeValue(null, "referenceId");
                            break;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    switch (tagName) {
                        case "Poi":
                            xml_name = parser.getAttributeValue(null, "name");
                            xml_birthDate = parser.getAttributeValue(null, "dob");
                            xml_emailSha = parser.getAttributeValue(null, "e");
                            xml_gender = parser.getAttributeValue(null, "gender");
                            xml_mobileSha = parser.getAttributeValue(null, "m");
                            break;
                        case "Poa":
                            xml_careOf = parser.getAttributeValue(null, "careof");
                            xml_country = parser.getAttributeValue(null, "country");
                            xml_district = parser.getAttributeValue(null, "dist");
                            xml_house = parser.getAttributeValue(null, "house");
                            xml_landmark = parser.getAttributeValue(null, "landmark");
                            xml_location = parser.getAttributeValue(null, "loc");
                            xml_pinCode = parser.getAttributeValue(null, "pc");
                            xml_po = parser.getAttributeValue(null, "po");
                            xml_state = parser.getAttributeValue(null, "state");
                            xml_street = parser.getAttributeValue(null, "street");
                            xml_subDistrict = parser.getAttributeValue(null, "subdist");
                            xml_vtc = parser.getAttributeValue(null, "vtc");
                            break;
                        case "Pht":
                            xml_photoByteCode = tempForXml;
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                    tempForXml = parser.getText();
                    break;
            }
            event = parser.next();
        }
        //completeVerification();
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

    /* complete verification of the file is here*/

    /*void completeVerification() {

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

        tv_status.setText(sb.toString());
    }*/

    public void askStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);
        }
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public static class SheetClass extends BottomSheetDialogFragment {

        MoneyTransaction.SheetClass sheetClass;
        View view;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.layout_kyc_data_agree, container, false);

            TextView tv_nameKyc, tv_mobileKyc, tv_eMailKyc, tv_addressKyc, tv_guardianKyc, tv_genderKyc, tv_dobKyc;

            tv_nameKyc = view.findViewById(R.id.tv_nameKyc);
            tv_mobileKyc = view.findViewById(R.id.tvb_mobileKyc);
            tv_eMailKyc = view.findViewById(R.id.tvb_emailKyc);
            tv_addressKyc = view.findViewById(R.id.tvb_addressKyc);
            tv_guardianKyc = view.findViewById(R.id.tvb_guardianKyc);
            tv_genderKyc = view.findViewById(R.id.tv_genderKyc);
            tv_dobKyc = view.findViewById(R.id.tv_dobKyc);

            Button btn_agreeKyc = view.findViewById(R.id.btn_agreeKyc);

            // setting user image here
            ImageView iv_userImageKyc = view.findViewById(R.id.iv_userImageKyc);
            byte[] decodedString = Base64.decode(xml_photoByteCode, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            iv_userImageKyc.setImageBitmap(decodedByte);

            // setting other user data here


            String firstColor = "<font color=#4fa5d5>";
            String secondColor = "</font> <font color=#222020>";
            String lastCloseColor = "</font>";

            String colorFulText = firstColor + "Name: " + secondColor
                    + xml_name + lastCloseColor;
            tv_nameKyc.setText(Html.fromHtml(colorFulText));

            colorFulText = firstColor + "Mobile: " + secondColor
                    + "8952872373" + lastCloseColor;
            tv_mobileKyc.setText(Html.fromHtml(colorFulText));

            colorFulText = firstColor + "Email: " + secondColor
                    + "ajaysharma400@gmail.com" + lastCloseColor;
            tv_eMailKyc.setText(Html.fromHtml(colorFulText));
            //if(emailShax.equals("")) tv_eMailKyc.setVisibility(View.GONE);

            colorFulText = firstColor + "Guardian: " + secondColor
                    + xml_careOf + lastCloseColor;
            tv_guardianKyc.setText(Html.fromHtml(colorFulText));

            colorFulText = firstColor + "DOB: " + secondColor
                    + xml_birthDate + lastCloseColor;
            tv_dobKyc.setText(Html.fromHtml(colorFulText));

            String gender = xml_gender;
            if (xml_gender.equals("M")) gender = "Male";
            else if (xml_gender.equals("F")) gender = "Female";
            colorFulText = firstColor + "Gender: " + secondColor
                    + gender + lastCloseColor;
            tv_genderKyc.setText(Html.fromHtml(colorFulText));

            String address = xml_house + " " + xml_street + ", " + xml_landmark + " " +
                    xml_location + ", " + xml_pinCode + ", " + xml_district + ", " + xml_state + ", " + xml_country;
            colorFulText = firstColor + "Address: " + secondColor
                    + address + lastCloseColor;
            tv_addressKyc.setText(Html.fromHtml(colorFulText));

            // if user agree then they click this button
            btn_agreeKyc.setOnClickListener(view -> {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

                View view1 = inflater.inflate(R.layout.layout_kyc_success, container, false);
                ImageView iv = view1.findViewById(R.id.iv_kyc_success);
                TextView tv1 = view1.findViewById(R.id.tv_kyc_success1);
                TextView tv2 = view1.findViewById(R.id.tv_kyc_success2);
                Button button = view1.findViewById(R.id.btn_kyc_success);

                final Animation animation = new AlphaAnimation(1, 0.8f); //to change visibility from visible to invisible
                animation.setDuration(1000); //1 second duration for each animation cycle
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.

                iv.startAnimation(animation);
                tv1.startAnimation(animation);
                tv2.startAnimation(animation);

                button.setOnClickListener(view2 -> {
                    startActivity(new Intent(getActivity(), WalletManage.class));
                    getActivity().finish();
                });

                alertBuilder.setView(view1);
                alertBuilder.setCancelable(false);
                alertBuilder.show();
            });

            return view;
        }
    }

}
