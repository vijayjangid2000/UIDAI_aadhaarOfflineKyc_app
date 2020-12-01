package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import net.lingala.zip4j.ZipFile;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String websiteLink = "https://resident.uidai.gov.in/offline-kyc";
    Button downloadBtn, verifyBtn;
    EditText nameET, mNumberET, shareCodeET;
    String userName, mobileNumber, shareCode;
    TextView instructionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameET = findViewById(R.id.name); // name given by user
        mNumberET = findViewById(R.id.number); // number given by user
        verifyBtn = findViewById(R.id.verfiyNow); // to after giving details to verify
        downloadBtn = findViewById(R.id.download); // for opening uidai website
        shareCodeET = findViewById(R.id.shareCode); // share code given here
        instructionTV = findViewById(R.id.instruction); // here we will notify the user


        // this checks the name/ number then check the latest zip file then unzip it
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = nameET.getText().toString().trim().toLowerCase();
                mobileNumber = mNumberET.getText().toString().trim().toLowerCase();
                shareCode = shareCodeET.getText().toString();

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Please Allow to use storage"
                            , Toast.LENGTH_LONG).show();
                    storage();
                } else if (userName.length() < 1)
                    Toast.makeText(MainActivity.this, "Please type your name"
                            , Toast.LENGTH_SHORT).show();
                else if (mobileNumber.length() != 10)
                    Toast.makeText(MainActivity.this, "Please type valid mobile number",
                            Toast.LENGTH_SHORT).show();
                else if (shareCode.length() != 4)
                    Toast.makeText(MainActivity.this, "Please type valid share code",
                            Toast.LENGTH_SHORT).show();
                else {

                    // if everything is good then we we will proceed with verification
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
                        instructionTV.setText("Please download the zip file from UIDAI" +
                                " website by clicking the download button above");
                        Toast.makeText(MainActivity.this, "File not found"
                                , Toast.LENGTH_SHORT).show();
                    } else unzip(file, shareCode); // giving address (using File Object) of zip file

                }
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom chrome tab here using the url
                Toast.makeText(MainActivity.this, "Please fill this form " +
                        "and follow instructions further", Toast.LENGTH_SHORT).show();
                CustomTabsIntent.Builder customTabIntent = new CustomTabsIntent.Builder();
                customTabIntent.build().intent.setPackage("com.android.chrome");
                customTabIntent.build().launchUrl(MainActivity.this, Uri.parse(websiteLink));
            }
        });

    }

    void unzip(File file, String password) {

        /* firstly check if the file is extracted or not */
        if (new File(file.getParent() + "/" + file.getName().split("\\.")[0]).exists()) {
            try {
                // if yes then no need to extract, directly show data
                showDataNow(file.getParent() + "/" + file.getName().split("\\.")[0]);
            } catch (Exception ignored) {
            }
            return;
        }

        // if not extracted then unzipping here
        ZipFile zipFile = new ZipFile(file.getAbsolutePath(),
                password.toCharArray());
        try {
            zipFile.extractAll(file.getParent() + "/" + file.getName().split("\\.")[0]);
            showDataNow(file.getParent() + "/" + file.getName().split("\\.")[0]);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,
                    "File was deleted or renamed or corrupted. Exception - " + e.getMessage()
                    , Toast.LENGTH_SHORT).show();
        }
    }

    // Here we will confirm the userData with the aadhar file;
    void showDataNow(String address) throws XmlPullParserException, IOException {
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
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String eltName = parser.getName();

            switch (event) {

                case XmlPullParser.START_TAG:
                    break;

                case XmlPullParser.END_TAG:

                    // when we reach Poi tag then we will verify name tag with the userName
                    if (eltName.equals("Poi")) {
                        String temp = parser.getAttributeValue(null, "name");
                        if (!temp.trim().toLowerCase().equals(userName)) instructionTV.setText(
                                "Verification Failed, Please check the details");
                        else {

                            /*if(!checkSignature(file)) instructionTV
                                    .setText("XML signature failed");*/

                            shareCodeET.setVisibility(View.GONE);
                            verifyBtn.setVisibility(View.GONE);
                            nameET.setVisibility(View.GONE);
                            mNumberET.setVisibility(View.GONE);
                            downloadBtn.setVisibility(View.GONE);

                            instructionTV.setText("Name - " + temp
                                    + "\n\nVerification Successful\n\n Thank you for registering");
                        }
                    }

                    break;
            }
            event = parser.next();

        }
    }

    /*asking for storage permission if not already given*/
    private static final int PERMISSION_REQUEST_CODE = 1;

    public void storage() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /*void checkSignature(File file) throws Exception {

        // Create a DOM XMLSignatureFactory that will be used to
    // generate the enveloped signature.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

    // Create a Reference to the enveloped document (in this case,
    // you are signing the whole document, so a URI of "" signifies
    // that, and also specify the SHA1 digest algorithm and
    // the ENVELOPED Transform.
        Reference ref = fac.newReference
                ("", fac.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList
                                (fac.newTransform
                                        (ConstraintSet.Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null, null);

    // Create the SignedInfo.
        SignedInfo si = fac.newSignedInfo
                (fac.newCanonicalizationMethod
                                (CanonicalizationMethod.INCLUSIVE,
                                        (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                        Collections.singletonList(ref));

        // Load the KeyStore and get the signing key and certificate.
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("mykeystore.jks"), "changeit".toCharArray());
        KeyStore.PrivateKeyEntry keyEntry =
                (KeyStore.PrivateKeyEntry) ks.getEntry
                        ("mykey", new KeyStore.PasswordProtection("changeit".toCharArray()));
        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        // Create the KeyInfo containing the X509Data.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        // Instantiate the document to be signed.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse
                (new FileInputStream("purchaseOrder.xml"));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext
                (keyEntry.getPrivateKey(), doc.getDocumentElement());

        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Marshal, generate, and sign the enveloped signature.
        signature.sign(dsc);

        // Output the resulting document.
        OutputStream os = new FileOutputStream("signedPurchaseOrder.xml");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));


        NodeList nl =
                doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        // Create a DOMValidateContext and specify a KeySelector
        // and document context.
        DOMValidateContext valContext = new DOMValidateContext
                (new X509KeySelector(), nl.item(0));

        // Unmarshal the XMLSignature.
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature.
        return signature.validate(valContext);

    }*/

}