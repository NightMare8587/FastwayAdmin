package com.consumers.fastwayadmin.NavFrags;

public class VendorBankClass {
    public String name,email,accountNumber,accountName,ifscCode,phoneNumber,vendorID,address;

    public VendorBankClass(String name, String email, String accountNumber, String accountName, String ifscCode, String phoneNumber,String vendorID,String address) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.ifscCode = ifscCode;
        this.phoneNumber = phoneNumber;
    }
}
