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
public class IncidentTimestamp {
    
    private String ds_name;
    private long timestamp;
    private int incident;

    public String getDs_name() {
        return ds_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getIncident() {
        return incident;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setIncident(int incident) {
        this.incident = incident;
    }    

}
