/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractdata.entities;

import java.util.Date;

/**
 *
 * @author AlbertSanchez
 */
public class IncidentDetailPhoneLoc {
    
    private String ds_name;
    private int key;
    private int type;
    private int pLoc;
    private double latitude;
    private double longitude;
    private float acc_x;
    private float acc_y;
    private float acc_z;
    private long timestamp;
    private float acc_68;
    private float gyr_a;
    private float gyr_b;
    private float gyr_c;
    
    public String getDs_name() {
        return ds_name;
    }
    
    public int getKey(){
        return key;
    }
    
    public int getType(){
        return type;
    }
    
    public int getpLoc() {
        return pLoc;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAcc_x() {
        return acc_x;
    }

    public float getAcc_y() {
        return acc_y;
    }

    public float getAcc_z() {
        return acc_z;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getAcc_68() {
        return acc_68;
    }

    public float getGyr_a() {
        return gyr_a;
    }

    public float getGyr_b() {
        return gyr_b;
    }

    public float getGyr_c() {
        return gyr_c;
    }
    
    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }
    
    public void setKey(int key){
        this.key = key;
    }
    
    public void setType(int type){
        this.type = type;
    }

    public void setpLoc(int pLoc) {
        this.pLoc = pLoc;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAcc_x(float acc_x) {
        this.acc_x = acc_x;
    }

    public void setAcc_y(float acc_y) {
        this.acc_y = acc_y;
    }

    public void setAcc_z(float acc_z) {
        this.acc_z = acc_z;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAcc_68(float acc_68) {
        this.acc_68 = acc_68;
    }

    public void setGyr_a(float gyr_a) {
        this.gyr_a = gyr_a;
    }

    public void setGyr_b(float gyr_b) {
        this.gyr_b = gyr_b;
    }

    public void setGyr_c(float gyr_c) {
        this.gyr_c = gyr_c;
    }
    
}
