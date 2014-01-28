package ru.ursmu.application.JsonObject;


import android.database.Cursor;

public class ChairItem {
    private static final String PREFIX_URL = "http://edu.ursmu.ru";
    private static int fac_in, name_in, phone_in, address_in, mail_in, cover_src_in, cover_title_in;

    private String mFaculty, mName, mPhone, mAddress, mMail, mCoverSrc, mCoverTitle;

    public ChairItem(Cursor c) {
        if (fac_in == 0 || name_in == 0 || phone_in == 0 || address_in == 0 || mail_in == 0 || cover_src_in == 0 || cover_title_in == 0) {
            fac_in = c.getColumnIndexOrThrow("faculty");
            name_in = c.getColumnIndexOrThrow("name");
            phone_in = c.getColumnIndexOrThrow("phone");
            address_in = c.getColumnIndexOrThrow("address");
            mail_in = c.getColumnIndexOrThrow("e_mail");
            cover_src_in = c.getColumnIndexOrThrow("cover_src");
            cover_title_in = c.getColumnIndexOrThrow("cover_title");
        }

        mFaculty = c.getString(fac_in);
        mName = c.getString(name_in);
        mPhone = c.getString(phone_in);
        mAddress = c.getString(address_in);
        mMail = c.getString(mail_in);
        mCoverSrc = c.getString(cover_src_in);
        mCoverTitle = c.getString(cover_title_in);
    }

    public String getFaculty() {
        return mFaculty;
    }

    public String getName() {
        return mName;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getMail() {
        return mMail;
    }

    public String getCoverSrc() {
        return PREFIX_URL +mCoverSrc;
    }

    public String getCoverTitle() {
        return mCoverTitle;
    }
}
