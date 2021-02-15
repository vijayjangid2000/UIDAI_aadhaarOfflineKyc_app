package com.vijayjangid.aadharkyc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Provider implements Parcelable {

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
    private String id;
    private String providerName;
    private String providerImage;
    private String serviceId;
    private String dealer;
    private String extraparams;

    public Provider() {
    }

    protected Provider(Parcel in) {
        id = in.readString();
        providerName = in.readString();
        providerImage = in.readString();
        serviceId = in.readString();
        extraparams = in.readString();
        dealer = in.readString();
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getExtraparams() {
        return extraparams;
    }

    public void setExtraparams(String extraparams) {
        this.extraparams = extraparams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderImage() {
        return providerImage;
    }

    public void setProviderImage(String providerImage) {
        this.providerImage = providerImage;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(providerName);
        dest.writeString(providerImage);
        dest.writeString(serviceId);
        dest.writeString(extraparams);
        dest.writeString(dealer);
    }
}
