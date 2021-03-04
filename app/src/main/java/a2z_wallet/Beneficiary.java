package a2z_wallet;

import android.os.Parcel;
import android.os.Parcelable;

public class Beneficiary implements Parcelable {

    private String imps;
    private String status;
    private String account;
    private String name;
    private String mobile;
    private String ifsc;
    private String bank;
    private String remitterName;
    private String last_success_name;
    private String id;
    public static final Creator<Beneficiary> CREATOR = new Creator<Beneficiary>() {
        @Override
        public Beneficiary createFromParcel(Parcel in) {
            return new Beneficiary(in);
        }

        @Override
        public Beneficiary[] newArray(int size) {
            return new Beneficiary[size];
        }
    };
    private String is_verified = "";
    private String last_success_date;

    protected Beneficiary(Parcel in) {
        imps = in.readString();
        status = in.readString();
        account = in.readString();
        name = in.readString();
        mobile = in.readString();
        ifsc = in.readString();
        bank = in.readString();
        remitterName = in.readString();
        last_success_name = in.readString();
        id = in.readString();
        last_success_date = in.readString();
        last_success_imps = in.readString();
    }

    private String last_success_imps;

    public String getRemitterName() {
        return remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public Beneficiary() {
    }

    public String getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }


    public String getImps() {
        return imps;
    }

    public void setImps(String imps) {
        this.imps = imps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getLast_success_name() {
        return last_success_name;
    }

    public void setLast_success_name(String last_success_name) {
        this.last_success_name = last_success_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_success_date() {
        return last_success_date;
    }

    public void setLast_success_date(String last_success_date) {
        this.last_success_date = last_success_date;
    }

    public String getLast_success_imps() {
        return last_success_imps;
    }

    public void setLast_success_imps(String last_success_imps) {
        this.last_success_imps = last_success_imps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imps);
        parcel.writeString(status);
        parcel.writeString(account);
        parcel.writeString(name);
        parcel.writeString(mobile);
        parcel.writeString(ifsc);
        parcel.writeString(bank);
        parcel.writeString(remitterName);
        parcel.writeString(last_success_name);
        parcel.writeString(id);
        parcel.writeString(last_success_date);
        parcel.writeString(last_success_imps);
    }
}
