package com.esnplus.digitalg10;
public class AlarmDevice {
    private int type,icon,language;
    private String name,phoneNumber;
    public static final int faLanguage = 1;
    public static final int enLanguage = 2;
    public static final int M52 = 1;
    public static final int GD10 = 2;
    public AlarmDevice(){
        this(GD10,"","",-1);
    }
    public AlarmDevice(int type, String name, String phoneNumber,int icon) {
        this.type = type;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.icon = icon;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public int getIcon() {
        return icon;
    }
    public void setLanguage(int language) {
        this.language = language;
    }
    public int getLanguage() {
        return language;
    }
}