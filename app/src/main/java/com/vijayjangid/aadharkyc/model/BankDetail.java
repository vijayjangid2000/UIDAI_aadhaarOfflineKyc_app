package com.vijayjangid.aadharkyc.model;

import java.io.Serializable;

public class BankDetail implements Serializable {
    private String id;
    private String mobile;
    private String accountNumber;
    private String ifsc;
    private String branchName;
    private String bankName;
    private String messageOne;
    private String messageTwo;
    private String status;
    private String charges;
    private String balance;
    private String aeps_bloack_amount;
    private String aeps_charge;
    private String beneName;
    private boolean isHide = true;
    private boolean isStatus = true;

    public BankDetail() {
    }

    public BankDetail(String id, String accountNumber, String ifsc, String branchName, String bankName,
                      String messageOne, String messageTwo) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.ifsc = ifsc;
        this.branchName = branchName;
        this.bankName = bankName;
        this.messageOne = messageOne;
        this.messageTwo = messageTwo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getBeneName() {
        return beneName;
    }

    public void setBeneName(String beneName) {
        this.beneName = beneName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAeps_bloack_amount() {
        return aeps_bloack_amount;
    }

    public void setAeps_bloack_amount(String aeps_bloack_amount) {
        this.aeps_bloack_amount = aeps_bloack_amount;
    }

    public String getAeps_charge() {
        return aeps_charge;
    }

    public void setAeps_charge(String aeps_charge) {
        this.aeps_charge = aeps_charge;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMessageOne() {
        return messageOne;
    }

    public void setMessageOne(String messageOne) {
        this.messageOne = messageOne;
    }

    public String getMessageTwo() {
        return messageTwo;
    }

    public void setMessageTwo(String messageTwo) {
        this.messageTwo = messageTwo;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }
}
