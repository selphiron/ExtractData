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
public class IncidentCoordinates {
    
    private String ds_name;
    private double latitude;
    private double longitude;
    private int incident;

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

}
