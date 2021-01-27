package com.vijayjangid.aadharkyc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.util.regex.Pattern;

public class AddCardObject extends AppCompatActivity implements View.OnClickListener {

    EditText et_cardNumber, et_cvv, et_mmyy;
    Button btn_addCard;
    ImageView iv_CardImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_object);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add new Card");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        idAndSetListeners();
        startTextWatchers();
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    void idAndSetListeners() {
        et_cardNumber = findViewById(R.id.et_cardNumberO);
        et_mmyy = findViewById(R.id.et_mmyyO);
        et_cvv = findViewById(R.id.et_cvvO);
        btn_addCard = findViewById(R.id.btn_addCard);

        btn_addCard.setOnClickListener(this);

        iv_CardImage = findViewById(R.id.iv_cardImageO);
        iv_CardImage.setVisibility(View.GONE);
    }

    void saveCardNow() {
        String cardNumber, mmyy, cvv;

        cardNumber = et_cardNumber.getText().toString();
        mmyy = et_mmyy.getText().toString();
        cvv = et_cvv.getText().toString();


        SharedPreferences mPrefs = getSharedPreferences("SavedCardInformation", MODE_PRIVATE);
        NewCard myObject = new NewCard(cardNumber, mmyy, "SBI Debit Card");
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        prefsEditor.putString("Card", json);
        prefsEditor.commit();
        Toast.makeText(this, "Card Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    void startTextWatchers() {


        et_cardNumber.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }

                if (s.length() == 19) {
                    et_mmyy.requestFocus();
                }

                setCardImage(s.toString().replaceAll("-", ""));
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });


        et_mmyy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 2) {

                    // this add slash between month and year
                    if (start == 2 && before == 1 && !s.toString().contains("/")) {
                        et_mmyy.setText("" + s.toString().charAt(0));
                        et_mmyy.setSelection(1);
                    } else {
                        et_mmyy.setText(s + "/");
                        et_mmyy.setSelection(3);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() == 2) {
                    try {
                        // this adds 12 at second position
                        int value = Integer.parseInt(String.valueOf(s.charAt(0)) + s.charAt(1));
                        if (value > 12) et_mmyy.setText("12/");
                        // only 1 to 12 allowed at second position
                        et_mmyy.setSelection(3);
                    } catch (Exception ignored) {

                    }

                } else if (s.length() == 5) {

                    String old = s.toString().substring(0, 2);
                    int value = Integer.parseInt(String.valueOf(s.charAt(3)) + s.charAt(4));
                    if (value < 20) {
                        et_mmyy.setText(old + "/" + 20);
                        Toast.makeText(AddCardObject.this, "Please enter valid year"
                                , Toast.LENGTH_SHORT).show();
                        et_mmyy.setSelection(5);
                    } else {
                        et_cvv.requestFocus();
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addCard:
                if (isDetailsCorrect()) saveCardNow();
                else Toast.makeText(this, "Enter Correct Info"
                        , Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean isDetailsCorrect() {

        String cardNumber, mmyy, cvv;

        cardNumber = et_cardNumber.getText().toString().trim();
        mmyy = et_mmyy.getText().toString();
        cvv = et_cvv.getText().toString();

        if (cardNumber.length() != (16 + 3) || cvv.length() != 3 || mmyy.length() != 5)
            return false;
        else return true;
    }

    void setCardImage(String cardNumber) {

        if (cardNumber == null || cardNumber.length() < 1) return;

        CardType cardName = CardType.detect(cardNumber);
        iv_CardImage.setVisibility(View.VISIBLE);

        if (cardName.equals(CardType.VISA)) {

            iv_CardImage.setImageDrawable(getDrawable(R.drawable.ic_visa));

        } else if (cardName.equals(CardType.MASTERCARD)) {

            iv_CardImage.setImageDrawable(getDrawable(R.drawable.ic_mastercard));

        } else if (cardName.equals(CardType.AMERICAN_EXPRESS)) {

            iv_CardImage.setImageDrawable(getDrawable(R.drawable.ic_american_express));

        } else if (cardName.equals(CardType.DINERS_CLUB)) {

            iv_CardImage.setImageDrawable(getDrawable(R.drawable.ic_dinners_club));

        } else if (cardName.equals(CardType.DISCOVER)) {

            iv_CardImage.setImageDrawable(getDrawable(R.drawable.ic_discover));

        } else if (cardName.equals(CardType.JCB)) {

            iv_CardImage.setImageDrawable(getDrawable(R.drawable.ic_jcb));

        } else {
            iv_CardImage.setVisibility(View.GONE);
        }
        /* you can also do things here like setting leftDrawable in the if statements*/
    }

    public boolean isValid(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public enum CardType {

        UNKNOWN,
        VISA("^4[0-9]{12}(?:[0-9]{3}){0,2}$"),
        MASTERCARD("^(?:5[1-5]|2(?!2([01]|20)|7(2[1-9]|3))[2-7])\\d{14}$"),
        AMERICAN_EXPRESS("^3[47][0-9]{13}$"),
        DINERS_CLUB("^3(?:0[0-5]\\d|095|6\\d{0,2}|[89]\\d{2})\\d{12,15}$"),
        DISCOVER("^6(?:011|[45][0-9]{2})[0-9]{12}$"),
        JCB("^(?:2131|1800|35\\d{3})\\d{11}$"),
        CHINA_UNION_PAY("^62[0-9]{14,17}$");

        private Pattern pattern;

        CardType() {
            this.pattern = null;
        }

        CardType(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public static CardType detect(String cardNumber) {

            for (CardType cardType : CardType.values()) {
                if (null == cardType.pattern) continue;
                if (cardType.pattern.matcher(cardNumber).matches()) return cardType;
            }

            return UNKNOWN;
        }

    }

    public class NewCard {

        String cardNumber;
        String expiryDate;
        String cardName;

        public NewCard(String cardNumber, String expiryDate, String cardName) {
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
            this.cardName = cardName;
        }

    }
}