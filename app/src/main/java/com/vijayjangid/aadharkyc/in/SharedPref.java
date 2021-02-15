package com.vijayjangid.aadharkyc.in;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String SHARED_PREF = "shared_preference";
    private static final String ID = "id";
    private static final String CHANGEPIN = "change_pin";
    private static final String TOKEN = "token";
    private static final String POPUP = "popup";
    private static final String POPUPSEE = "popupsee";
    private static final String NAME = "name";
    private static final String FATHER = "father";
    private static final String DOB = "dob";
    private static final String EMAIL = "email";
    private static final String USER_BALANCE = "user_balance";
    private static final String MOBILE = "mobile";
    private static final String OTP_NUMBER = "otp_number";
    private static final String PROFILE_PIC = "profile_pic";
    private static final String PANCARD_PIC = "pancard_pic";
    private static final String ADHAAR_PIC = "adhaar_pic";
    private static final String ADHAAR_BACK_PIC = "adhaar_back_pic";
    private static final String ROLL_ID = "roll_id";
    private static final String GENDER = "gender";
    private static final String STATE_ID = "state_id";
    private static final String CITY = "city";
    private static final String SHOP_NAME = "shop_name";
    private static final String ADDRESS = "address";
    // private static final String ROLL_ID = "roll_id";
    //private static final String ROLL_ID = "roll_id";
    private static final String SHOP_ADRESS = "shop_address";
    private static final String JOINING_DATE = "joining_date";
    private static final String LAST_UPDATE = "last_update";
    private static final String AUTO_LOGIN = "auto_login";
    private static final String LOGIN_PASSWORD = "login_password";
    private static final String APP_START_COUNT = "app_start_count";
    private static SharedPref sharedPref;
    private static SharedPreferences mSharedPref;
    //kyc_status
    String KYC_STATUS = "kyc_status";
    ///pan number
    String PAN = "pan";
    ///adhaar number
    String ADHAAR = "adhaar";
    ///pincode
    String PINCODE = "pincode";

    private SharedPref(Context context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(SHARED_PREF, Activity.MODE_PRIVATE);
        }
    }

    public static SharedPref getInstance(Context context) {
        if (sharedPref == null) {
            sharedPref = new SharedPref(context);
        }
        return sharedPref;
    }

    public int getCHANGEPIN() {
        return mSharedPref.getInt(CHANGEPIN, 0);
    }

    public void setChangepin(int changepin) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(CHANGEPIN, changepin);
        editor.apply();
    }

    public String getGender() {
        return mSharedPref.getString(GENDER, "");
    }

    public void setGender(String gender) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(GENDER, gender);
        editor.apply();
    }

    public String getSTATEID() {
        return mSharedPref.getString(STATE_ID, "");
    }

    public void setSTATEID(String state_id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(STATE_ID, state_id);
        editor.apply();
    }

    public String getCity() {
        return mSharedPref.getString(CITY, "");
    }

    public void setCity(String city) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(CITY, city);
        editor.apply();
    }

    public void delete_id() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ID);
        editor.apply();
    }

    public int getId() {
        return mSharedPref.getInt(ID, 0);
    }

    //////////// id /////////////////////
    public void setId(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(ID, id);
        editor.apply();
    }

    public void deleteRollId() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ROLL_ID);
        editor.apply();
    }

    public int getRollId() {
        return mSharedPref.getInt(ROLL_ID, 0);
    }

    //////////// id /////////////////////
    public void setRollId(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(ROLL_ID, id);
        editor.apply();
    }

    public boolean getPopupSee() {
        return mSharedPref.getBoolean(POPUPSEE, false);
    }

    //
    public void setPopupSee(boolean popup) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(POPUPSEE, popup);
        editor.apply();
    }

    public String getPopup() {
        return mSharedPref.getString(POPUP, "");
    }

    //notification
    public void setPopup(String popup) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(POPUP, popup);
        editor.apply();
    }

    public void deletToken() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(TOKEN);
        editor.apply();
    }

    public String getToken() {
        return mSharedPref.getString(TOKEN, "");
    }

    //////////// token /////////////////////
    public void setToken(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public int getKYC_STATUS() {
        return mSharedPref.getInt(KYC_STATUS, 0);
    }

    public void setKYC_STATUS(int kyc_status) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(KYC_STATUS, kyc_status);
        editor.apply();
    }

    public String getPan() {
        return (mSharedPref.getString(PAN, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(PAN, ""));
    }

    public void setPan(String pan) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PAN, pan);
        editor.apply();
    }

    public String getAdhaar() {
        return (mSharedPref.getString(ADHAAR, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(ADHAAR, ""));
    }

    public void setAdhaar(String adhaar) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADHAAR, adhaar);
        editor.apply();
    }

    public String getPINCODE() {

        return (mSharedPref.getString(PINCODE, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(PINCODE, ""));
    }

    public void setPINCODE(String pincode) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PINCODE, pincode);
        editor.apply();
    }

    public String getFather() {
        return (mSharedPref.getString(FATHER, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(FATHER, ""));
    }

    public void setFather(String father) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(FATHER, father);
        editor.apply();
    }

    public String getDob() {
        return (mSharedPref.getString(DOB, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(DOB, ""));
    }

    public void setDob(String dob) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(DOB, dob);
        editor.apply();
    }

    public void deleteName() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(NAME);
        editor.apply();
    }

    public String getName() {
        return (mSharedPref.getString(NAME, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(NAME, ""));
    }

    //////////// name /////////////////////
    public void setName(String name) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public void deleteEmail() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(EMAIL);
        editor.apply();
    }

    public String getEmail() {
        return (mSharedPref.getString(EMAIL, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(EMAIL, ""));
    }

    //////////// email /////////////////////
    public void setEmail(String email) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public void deleteUserBalance() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(USER_BALANCE);
        editor.apply();
    }

    public String getUserBalance() {
        return mSharedPref.getString(USER_BALANCE, "0.00");
    }

    //////////// user balance /////////////////////
    public void setUserBalance(String userBalance) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(USER_BALANCE, userBalance);
        editor.apply();
    }

    public void deleteProfilePick() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(PROFILE_PIC);
        editor.apply();
    }

    public String getProfilePic() {
        return mSharedPref.getString(PROFILE_PIC, "");
    }

    //////////// profile pic /////////////////////
    public void setProfilePic(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PROFILE_PIC, profilePic);
        editor.apply();
    }

    public String getPANCARD_PIC() {
        return mSharedPref.getString(PANCARD_PIC, "");
    }

    //////////// pancard pic /////////////////////
    public void setPANCARD_PIC(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PANCARD_PIC, profilePic);
        editor.apply();
    }

    public String getAdhaarPic() {
        return mSharedPref.getString(ADHAAR_PIC, "");
    }

    //////////// adhaar pic /////////////////////
    public void setAdhaarPic(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADHAAR_PIC, profilePic);
        editor.apply();
    }

    public String getADHAAR_BACK_PIC() {
        return mSharedPref.getString(ADHAAR_BACK_PIC, "");
    }

    //////////// adhaar back pic /////////////////////
    public void setADHAAR_BACK_PIC(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADHAAR_BACK_PIC, profilePic);
        editor.apply();
    }

    public void deleteMobile() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(MOBILE);
        editor.apply();
    }

    public String getMobile() {
        return mSharedPref.getString(MOBILE, "");
    }

    //////////// mobile /////////////////////
    public void setMobile(String mobile) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(MOBILE, mobile);
        editor.apply();
    }

    public void deleteOtp() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(OTP_NUMBER);
        editor.apply();
    }

    public int getOtp() {
        return mSharedPref.getInt(OTP_NUMBER, 0);
    }

    //////////// otp /////////////////////
    public void setOtp(int otp) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(OTP_NUMBER, otp);
        editor.apply();
    }

    public void deleteShopName() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(SHOP_NAME);
        editor.apply();
    }

    public String getShopName() {
        return (mSharedPref.getString(SHOP_NAME, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(SHOP_NAME, ""));
    }

    //////////// shop name /////////////////////
    public void setShopName(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(SHOP_NAME, token);
        editor.apply();
    }

    public void deleteShopAddress() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(SHOP_ADRESS);
        editor.apply();
    }

    public String getShopAdress() {

        return (mSharedPref.getString(SHOP_ADRESS, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(SHOP_ADRESS, ""));
    }

    //////////// shop address /////////////////////
    public void setShopAdress(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(SHOP_ADRESS, token);
        editor.apply();
    }

    public void deleteAddress() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ADDRESS);
        editor.apply();
    }

    public String getAddress() {
        return mSharedPref.getString(ADDRESS, "");
    }

    //////////// shop address /////////////////////
    public void setAddress(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADDRESS, token);
        editor.apply();
    }

    public void deleteJoiningDate() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(JOINING_DATE);
        editor.apply();
    }

    public String getJoiningDate() {
        return mSharedPref.getString(JOINING_DATE, "");
    }

    //////////// joining date /////////////////////
    public void setJoiningDate(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(JOINING_DATE, token);
        editor.apply();
    }

    public void deleteLastUpdate() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(LAST_UPDATE);
        editor.apply();
    }

    public String getLastUpdate() {
        return mSharedPref.getString(LAST_UPDATE, "");
    }

    //////////// last update /////////////////////
    public void setLastUpdate(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(LAST_UPDATE, token);
        editor.apply();
    }

    public void deleteAppStartCount() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(APP_START_COUNT);
        editor.apply();
    }

    public int getAppStartCount() {
        return mSharedPref.getInt(APP_START_COUNT, 1);
    }

    //////////// app start count /////////////////////
    public void setAppStartCount(int appStartCount) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(APP_START_COUNT, appStartCount);
        editor.apply();
    }

    public void deleteLoginPassword() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(LOGIN_PASSWORD);
        editor.apply();
    }

    public String getLoginPassword() {
        return mSharedPref.getString(LOGIN_PASSWORD, "");
    }

    public void setLoginPassword(String period) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(LOGIN_PASSWORD, period);
        editor.apply();
    }

    public void deleteAutoLogin() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(AUTO_LOGIN);
        editor.apply();
    }

    public boolean getAutoLogin() {
        return mSharedPref.getBoolean(AUTO_LOGIN, false);
    }

    public void setAutoLogin(boolean autoLogin) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(AUTO_LOGIN, autoLogin);
        editor.apply();
    }


}
