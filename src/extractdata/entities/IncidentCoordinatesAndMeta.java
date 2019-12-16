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
public class IncidentCoordinatesAndMeta {
    
    private String ds_name;
    private double latitude;
    private double longitude;
    private int incident;
    private int pLoc;
    private int bikeType;

    public String getDs_name() {
        return ds_name;
    }

    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }

    public int getIncident() {
        return incident;
    }

    public int getpLoc() {
        return pLoc;
    }

    public int getBikeType() {
        return bikeType;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public void setIncident(int incident) {
        this.incident = incident;
    }    

    public void setpLoc(int pLoc) {
        this.pLoc = pLoc;
    }

    public void setBikeType(int bikeType) {
        this.bikeType = bikeType;
    }
}
