package com.consumers.fastwayadmin.Info.RestaurantDocuments;

public class ResDocuments {
    public String pan,adhaar,fssai,gst,resProof;

    public ResDocuments(String pan, String adhaar, String fssai, String gst,String resProof) {
        this.pan = pan;
        this.resProof = resProof;
        this.adhaar = adhaar;
        this.fssai = fssai;
        this.gst = gst;
    }
}
