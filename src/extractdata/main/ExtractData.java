/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractdata.main;

import extractdata.entities.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.hssf.usermodel.*;


/**
 *
 * @author AlbertSanchez
 */
public class ExtractData {   
    
    // Excel Extraction
    private static final boolean EXTRACTION = true;
    
    // Include Incidents with user TAG
    private static final boolean userTAG = true;
    
    // Folder path (where the dataset is)
    private static String folder = "/Users/AlbertSanchez/Desktop/TestDataset1";
        
    public static void main(String[] args) throws IOException 
    {   
        if(!folder.endsWith("/")) folder += "/";
        
        System.out.println("Begining the Data Extraction");
        System.out.println("...");
        System.out.println("Searching files in " + folder + " ...");
        
        List<String> fileNames = getFileNames(folder);
        
        if (fileNames.isEmpty())
            System.out.println("0 files founded. Exiting program...");
        else
        {
            System.out.println(fileNames.size() + " files found");
            System.out.println("Reading files...");

            List<Incident> incidents = new ArrayList<>();

            for (String file : fileNames)
            {
                List<Incident> i = readIncidents(file);
                incidents.addAll(i);
            }
            System.out.println("Files readed. " + incidents.size() + " incidents found");

            ArrayList<List<List<IncidentDetailPhoneLoc_BikeType>>> detailIncidentsWithMetadata = new ArrayList<>();

            // Get incidents (filename, type, Latitude, Longitude, phoneLocation and bikeType)
            List<IncidentCoordinatesAndMeta> tempIncidents = new ArrayList<>();
            for (Incident i : incidents)
            {
                if (i.getIncident() != 0)
                {
                    IncidentCoordinatesAndMeta iT = new IncidentCoordinatesAndMeta();
                    iT.setDs_name(i.getDs_name());
                    iT.setIncident(i.getIncident());
                    iT.setLatitude(i.getLatitude());
                    iT.setLongitude(i.getLongitude());
                    iT.setpLoc(i.getpLoc());
                    iT.setBikeType(i.getBike());
                    tempIncidents.add(iT);
                }
            }

            detailIncidentsWithMetadata = readDetailwithMetadata(tempIncidents);


            if (EXTRACTION)
            {
                System.out.println("Generating XLS Incidents file");
                String xlsName = writeXLSIncidentsFile(folder, incidents);
                System.out.println("XLS file generated. Name: " + xlsName);
                
                // Comment next 3 lines if error "Invalid row number (65536) outside allowable range (0..65535)" appears
                //System.out.println("Generating XLS Detail file");
                //String xlsDetailNameMetadata = writeXLSDetailFile(folder, detailIncidentsWithMetadata);
                //System.out.println("XLS file generated. Name: " + xlsDetailNameMetadata);
                
                // Uncomment next 3 lines if error "Invalid row number (65536) outside allowable range (0..65535)" appears
                System.out.println("Generating CSV Detail file");
                String csvDetailNameMetadata = writeCSVDetailFile(folder, detailIncidentsWithMetadata);
                System.out.println("CSV file generated. Name: " + csvDetailNameMetadata);
            }
        }
    }
    
    // Gets all the filenames of the directory
    private static List<String> getFileNames(String directory)
    {
        List<String> results = new ArrayList<>();
        File[] files = new File(directory).listFiles();

        for (File file : files) 
        {
            if (file.isFile()) 
                if(!file.getName().startsWith("."))
                    results.add(directory + file.getName());
        }
        return results;
    }
    
    // Reads the incidents listed in the file (lines before the '=========================')
    private static List<Incident> readIncidents(String file) throws IOException
    {   
        List<Incident> incidents = new ArrayList<>();
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String line;
        
        String[] s = file.split("/");
        String fileName = s[s.length-1];

        br.readLine(); //Read the 1st line which is <app version>#<file version>
        br.readLine(); //Read the 2nd line which are the headers
        line = br.readLine();
        while (!line.equals("")) 
        {
            Incident incident = new Incident();
            incident.setDs_name(fileName);
            String[] incidentFields = line.split(",",-1);
            incident.setKey(Integer.parseInt(incidentFields[0]));
            incident.setLatitude(Double.parseDouble(incidentFields[1]));
            incident.setLongitude(Double.parseDouble(incidentFields[2]));
            incident.setTimestamp(Long.parseLong(incidentFields[3]));
            incident.setBike(Integer.parseInt(incidentFields[4]));
            if(incidentFields[5].equals("1"))
                incident.setChildCheckBox(true);
            else
                incident.setChildCheckBox(false);
            if(incidentFields[6].equals("1"))
                incident.setTrailerCheckBox(true);
            else
                incident.setTrailerCheckBox(false); 
            incident.setpLoc(Integer.parseInt(incidentFields[7]));
            if(incidentFields[8].equals(""))
                incident.setIncident(0);
            else
                incident.setIncident(Integer.parseInt(incidentFields[8]));
            if(incidentFields[9].equals("1"))
                incident.setI1(true);
            else
                incident.setI1(false);
            if(incidentFields[10].equals("1"))
                incident.setI2(true);
            else
                incident.setI2(false);
            if(incidentFields[11].equals("1"))
                incident.setI3(true);
            else
                incident.setI3(false);
            if(incidentFields[12].equals("1"))
                incident.setI4(true);
            else
                incident.setI4(false);
            if(incidentFields[13].equals("1"))
                incident.setI5(true);
            else
                incident.setI5(false);
            if(incidentFields[14].equals("1"))
                incident.setI6(true);
            else
                incident.setI6(false);
            if(incidentFields[15].equals("1"))
                incident.setI7(true);
            else
                incident.setI7(false);
            if(incidentFields[16].equals("1"))
                incident.setI8(true);
            else
                incident.setI8(false);
            if(incidentFields[17].equals("1"))
                incident.setI9(true);
            else
                incident.setI9(false);
            if(incidentFields[18].equals("1"))
                incident.setScary(true);
            else
                incident.setScary(false);
            
            String description = "";
            if(incidentFields.length > 21)
            {
                // We have commas in description field so we add all in description except the last one (I10 term)
                for(int j=19;j<incidentFields.length-1;j++)
                {
                    description += incidentFields[j];
                }
                incident.setDesc(description);
                
                if(incidentFields[incidentFields.length-1].equals("1"))
                    incident.setI10(true);
                else
                    incident.setI10(false);
            }
            else if (incidentFields.length == 21)
            {
                description = incidentFields[19];
                incident.setDesc(description);
                if(incidentFields[20].equals("1"))
                    incident.setI10(true);
                else
                    incident.setI10(false);
            }
            else 
            {
                incident.setDesc(incidentFields[incidentFields.length-2]);
                incident.setI10(false);
            }
            
                    
            // Include userTAG incidents
            if(userTAG)
            {
                if (incident.getIncident() != 0)
                    incidents.add(incident);
            }    
            else
            {
                if (incident.getIncident() != 0 && incident.getTimestamp() != 1337)
                    incidents.add(incident);
            }
            
            line = br.readLine();
        }    
        return incidents;
    }
    
    // Reads the ride data (lines after the '=========================') adding 
    // a field where metadata for each of the incidents in the ride is saved (Phone location & Bike type)
    private static ArrayList<List<List<IncidentDetailPhoneLoc_BikeType>>> readDetailwithMetadata(List<IncidentCoordinatesAndMeta> incidents) throws IOException
    {
        ArrayList<List<List<IncidentDetailPhoneLoc_BikeType>>> detailIncidents = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d1 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d2 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d3 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d4 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d5 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d6 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d7 = new ArrayList<>();
        List<List<IncidentDetailPhoneLoc_BikeType>> d8 = new ArrayList<>();
        double prevLatDetailIncident=0;
        double prevLonDetailIncident=0;
        float prevAcc_68DetailIncident=0;
        float prevGyr_aDetailIncident=0;
        float prevGyr_bDetailIncident=0;
        float prevGyr_cDetailIncident=0; 
        int i=-1;
        boolean gpsLost = false, fileWithWrongFormat = false;

        // For each incident, we take the filename where the incident is and we 
        // scan it to save all the data
        for(IncidentCoordinatesAndMeta iT : incidents)
        {
            fileWithWrongFormat = false;
            i++;
            List<IncidentDetailPhoneLoc_BikeType> idet = new ArrayList<>();
            IncidentDetailPhoneLoc_BikeType detailIncident = null;
            FileReader r1 = new FileReader(folder + iT.getDs_name());
            FileReader r2 = new FileReader(folder + iT.getDs_name());
            LineNumberReader lnr = new LineNumberReader(r1);
            BufferedReader br = new BufferedReader(r2);

            int gpsLine = 1;
            
            // Read until header of the ride data
            String line = lnr.readLine();
            while(!line.equals("lat,lon,X,Y,Z,timeStamp,acc,a,b,c"))
            {
                line = lnr.readLine();
                gpsLine++;
                if(line.equals("lat,lon,X,Y,Z,timeStamp")) 
                {
                    fileWithWrongFormat = true;
                    break;
                }
            }
            
            if (!fileWithWrongFormat)
            {
                line = lnr.readLine();

                if (line != null)
                {
                    String[] incidentFields = line.split(",",-1); 

                    prevLatDetailIncident = Double.parseDouble(incidentFields[0]);
                    prevLonDetailIncident = Double.parseDouble(incidentFields[1]);

                    //Searching previous GPS Coordinates of the Incident
                    while((iT.getLatitude() != prevLatDetailIncident) ||
                            (iT.getLongitude() != prevLonDetailIncident))
                    {
                        //GPS Coordinates found
                        if (!incidentFields[0].equals(""))
                            gpsLine = lnr.getLineNumber() - 1; 

                        line = lnr.readLine();
                        incidentFields = line.split(",",-1); 
                        if (!incidentFields[0].equals("") && !incidentFields[1].equals(""))
                        {
                            prevLatDetailIncident = Double.parseDouble(incidentFields[0]);
                            prevLonDetailIncident = Double.parseDouble(incidentFields[1]);
                        }
                    }

                    //Going to the previous GPS Coordinates
                    for(int z=0; z < gpsLine; z++)
                        br.readLine();

                    line = br.readLine();
                    incidentFields = line.split(",",-1); 

                    //Read values before the central incident point
                    while((iT.getLatitude() != Double.parseDouble(incidentFields[0])) ||
                         (iT.getLongitude() != Double.parseDouble(incidentFields[1])))
                    {

                        detailIncident = new IncidentDetailPhoneLoc_BikeType();

                        detailIncident.setDs_name(iT.getDs_name());
                        detailIncident.setKey(i);
                        detailIncident.setType(iT.getIncident());
                        detailIncident.setpLoc(iT.getpLoc());
                        detailIncident.setBikeType(iT.getBikeType());

                        if (incidentFields[0].equals(""))
                            detailIncident.setLatitude(prevLatDetailIncident);
                        else
                        {
                            detailIncident.setLatitude(Double.parseDouble(incidentFields[0]));
                            prevLatDetailIncident = detailIncident.getLatitude();
                        }

                        if (incidentFields[1].equals(""))
                            detailIncident.setLongitude(prevLonDetailIncident);
                        else
                        {
                            detailIncident.setLongitude(Double.parseDouble(incidentFields[1]));
                            prevLonDetailIncident = detailIncident.getLongitude();
                        }

                        detailIncident.setAcc_x(Float.parseFloat(incidentFields[2]));
                        detailIncident.setAcc_y(Float.parseFloat(incidentFields[3]));
                        detailIncident.setAcc_z(Float.parseFloat(incidentFields[4]));
                        detailIncident.setTimestamp(Long.parseLong(incidentFields[5]));
                        if (incidentFields[6].equals(""))
                            detailIncident.setAcc_68(prevAcc_68DetailIncident);
                        else
                        {
                            detailIncident.setAcc_68(Float.parseFloat(incidentFields[6]));
                            prevAcc_68DetailIncident = detailIncident.getAcc_68();
                        }
                        if (incidentFields[7].equals(""))
                            detailIncident.setGyr_a(prevGyr_aDetailIncident);
                        else
                        {
                            detailIncident.setGyr_a(Float.parseFloat(incidentFields[7]));
                            prevGyr_aDetailIncident = detailIncident.getGyr_a();
                        }
                        if (incidentFields[8].equals(""))
                            detailIncident.setGyr_b(prevGyr_bDetailIncident);
                        else
                        {
                            detailIncident.setGyr_b(Float.parseFloat(incidentFields[8]));
                            prevGyr_bDetailIncident = detailIncident.getGyr_b();
                        }
                        if (incidentFields[9].equals(""))
                            detailIncident.setGyr_c(prevGyr_cDetailIncident);
                        else
                        {
                            detailIncident.setGyr_c(Float.parseFloat(incidentFields[9]));
                            prevGyr_cDetailIncident = detailIncident.getGyr_c();
                        }
                        idet.add(detailIncident);

                        line = br.readLine();
                        if (line == null)
                            break;

                        incidentFields = line.split(",",-1);   

                        if (incidentFields[0].equals(""))
                            incidentFields[0] = String.valueOf(prevLatDetailIncident);
                        if (incidentFields[1].equals(""))
                            incidentFields[1] = String.valueOf(prevLonDetailIncident);
                    }

                    //Read values of the central incident point
                    detailIncident = new IncidentDetailPhoneLoc_BikeType();

                    detailIncident.setDs_name(iT.getDs_name());
                    detailIncident.setKey(i);
                    detailIncident.setType(iT.getIncident());
                    detailIncident.setpLoc(iT.getpLoc());
                    detailIncident.setBikeType(iT.getBikeType());

                    if (incidentFields[0].equals(""))
                        detailIncident.setLatitude(prevLatDetailIncident);
                    else
                    {
                        detailIncident.setLatitude(Double.parseDouble(incidentFields[0]));
                        prevLatDetailIncident = detailIncident.getLatitude();
                    }

                    if (incidentFields[1].equals(""))
                        detailIncident.setLongitude(prevLonDetailIncident);
                    else
                    {
                        detailIncident.setLongitude(Double.parseDouble(incidentFields[1]));
                        prevLonDetailIncident = detailIncident.getLongitude();
                    }

                    detailIncident.setAcc_x(Float.parseFloat(incidentFields[2]));
                    detailIncident.setAcc_y(Float.parseFloat(incidentFields[3]));
                    detailIncident.setAcc_z(Float.parseFloat(incidentFields[4]));
                    detailIncident.setTimestamp(Long.parseLong(incidentFields[5]));
                    if (incidentFields[6].equals(""))
                        detailIncident.setAcc_68(prevAcc_68DetailIncident);
                    else
                    {
                        detailIncident.setAcc_68(Float.parseFloat(incidentFields[6]));
                        prevAcc_68DetailIncident = detailIncident.getAcc_68();
                    }
                    if (incidentFields[7].equals(""))
                        detailIncident.setGyr_a(prevGyr_aDetailIncident);
                    else
                    {
                        detailIncident.setGyr_a(Float.parseFloat(incidentFields[7]));
                        prevGyr_aDetailIncident = detailIncident.getGyr_a();
                    }
                    if (incidentFields[8].equals(""))
                        detailIncident.setGyr_b(prevGyr_bDetailIncident);
                    else
                    {
                        detailIncident.setGyr_b(Float.parseFloat(incidentFields[8]));
                        prevGyr_bDetailIncident = detailIncident.getGyr_b();
                    }
                    if (incidentFields[9].equals(""))
                        detailIncident.setGyr_c(prevGyr_cDetailIncident);
                    else
                    {
                        detailIncident.setGyr_c(Float.parseFloat(incidentFields[9]));
                        prevGyr_cDetailIncident = detailIncident.getGyr_c();
                    }
                    idet.add(detailIncident);

                    line = br.readLine();
                    if (line == null)
                        break;

                    incidentFields = line.split(",",-1); 

                    detailIncident = null;

                    //Read values after the central incident point
                    while(incidentFields[0].equals("") && incidentFields[1].equals(""))
                    {
                        detailIncident = new IncidentDetailPhoneLoc_BikeType();

                        detailIncident.setDs_name(iT.getDs_name());
                        detailIncident.setKey(i);
                        detailIncident.setType(iT.getIncident());
                        detailIncident.setpLoc(iT.getpLoc());
                        detailIncident.setBikeType(iT.getBikeType());

                        if (incidentFields[0].equals(""))
                            detailIncident.setLatitude(prevLatDetailIncident);
                        else
                        {
                            detailIncident.setLatitude(Double.parseDouble(incidentFields[0]));
                            prevLatDetailIncident = detailIncident.getLatitude();
                        }

                        if (incidentFields[1].equals(""))
                            detailIncident.setLongitude(prevLonDetailIncident);
                        else
                        {
                            detailIncident.setLongitude(Double.parseDouble(incidentFields[1]));
                            prevLonDetailIncident = detailIncident.getLongitude();
                        }

                        detailIncident.setAcc_x(Float.parseFloat(incidentFields[2]));
                        detailIncident.setAcc_y(Float.parseFloat(incidentFields[3]));
                        detailIncident.setAcc_z(Float.parseFloat(incidentFields[4]));
                        detailIncident.setTimestamp(Long.parseLong(incidentFields[5]));
                        if (incidentFields[6].equals(""))
                            detailIncident.setAcc_68(prevAcc_68DetailIncident);
                        else
                        {
                            detailIncident.setAcc_68(Float.parseFloat(incidentFields[6]));
                            prevAcc_68DetailIncident = detailIncident.getAcc_68();
                        }
                        if (incidentFields[7].equals(""))
                            detailIncident.setGyr_a(prevGyr_aDetailIncident);
                        else
                        {
                            detailIncident.setGyr_a(Float.parseFloat(incidentFields[7]));
                            prevGyr_aDetailIncident = detailIncident.getGyr_a();
                        }
                        if (incidentFields[8].equals(""))
                            detailIncident.setGyr_b(prevGyr_bDetailIncident);
                        else
                        {
                            detailIncident.setGyr_b(Float.parseFloat(incidentFields[8]));
                            prevGyr_bDetailIncident = detailIncident.getGyr_b();
                        }
                        if (incidentFields[9].equals(""))
                            detailIncident.setGyr_c(prevGyr_cDetailIncident);
                        else
                        {
                            detailIncident.setGyr_c(Float.parseFloat(incidentFields[9]));
                            prevGyr_cDetailIncident = detailIncident.getGyr_c();
                        }
                        idet.add(detailIncident);

                        line = br.readLine();
                        if (line == null)
                            break;

                        incidentFields = line.split(",",-1); 
                    }

                    detailIncident = null;

                    //Read last GPS coordinates data
                    detailIncident = new IncidentDetailPhoneLoc_BikeType();

                    detailIncident.setDs_name(iT.getDs_name());
                    detailIncident.setKey(i);
                    detailIncident.setType(iT.getIncident());
                    detailIncident.setpLoc(iT.getpLoc());
                    detailIncident.setBikeType(iT.getBikeType());

                    if (incidentFields[0].equals(""))
                        detailIncident.setLatitude(prevLatDetailIncident);
                    else
                    {
                        detailIncident.setLatitude(Double.parseDouble(incidentFields[0]));
                        prevLatDetailIncident = detailIncident.getLatitude();
                    }

                    if (incidentFields[1].equals(""))
                        detailIncident.setLongitude(prevLonDetailIncident);
                    else
                    {
                        detailIncident.setLongitude(Double.parseDouble(incidentFields[1]));
                        prevLonDetailIncident = detailIncident.getLongitude();
                    }

                    detailIncident.setAcc_x(Float.parseFloat(incidentFields[2]));
                    detailIncident.setAcc_y(Float.parseFloat(incidentFields[3]));
                    detailIncident.setAcc_z(Float.parseFloat(incidentFields[4]));
                    detailIncident.setTimestamp(Long.parseLong(incidentFields[5]));
                    if (incidentFields[6].equals(""))
                        detailIncident.setAcc_68(prevAcc_68DetailIncident);
                    else
                    {
                        detailIncident.setAcc_68(Float.parseFloat(incidentFields[6]));
                        prevAcc_68DetailIncident = detailIncident.getAcc_68();
                    }
                    if (incidentFields[7].equals(""))
                        detailIncident.setGyr_a(prevGyr_aDetailIncident);
                    else
                    {
                        detailIncident.setGyr_a(Float.parseFloat(incidentFields[7]));
                        prevGyr_aDetailIncident = detailIncident.getGyr_a();
                    }
                    if (incidentFields[8].equals(""))
                        detailIncident.setGyr_b(prevGyr_bDetailIncident);
                    else
                    {
                        detailIncident.setGyr_b(Float.parseFloat(incidentFields[8]));
                        prevGyr_bDetailIncident = detailIncident.getGyr_b();
                    }
                    if (incidentFields[9].equals(""))
                        detailIncident.setGyr_c(prevGyr_cDetailIncident);
                    else
                    {
                        detailIncident.setGyr_c(Float.parseFloat(incidentFields[9]));
                        prevGyr_cDetailIncident = detailIncident.getGyr_c();
                    }
                    idet.add(detailIncident);

                    if (idet.size() > 100)
                        gpsLost = true;

                    if (!idet.isEmpty() && !gpsLost)
                    {    
                        switch (iT.getIncident())
                        {
                            case 1:
                                d1.add(idet);
                                break;
                            case 2:
                                d2.add(idet);
                                break;
                            case 3:
                                d3.add(idet);
                                break;
                            case 4:
                                d4.add(idet);
                                break;
                            case 5:
                                d5.add(idet);
                                break;
                            case 6:
                                d6.add(idet);
                                break;
                            case 7:
                                d7.add(idet);
                                break;
                            case 8:
                                d8.add(idet);
                                break;
                            default:
                                break;
                        }
                        //System.out.println("Added incident " + i);
                    }
                    gpsLost = false;
                }
                else System.out.println("Filename: " + iT.getDs_name() + " is empty");
            }
            else System.out.println("Filename: " + iT.getDs_name() + " has a wrong format");
        }
        detailIncidents.add(d1);
        detailIncidents.add(d2);
        detailIncidents.add(d3);
        detailIncidents.add(d4);
        detailIncidents.add(d5);
        detailIncidents.add(d6);
        detailIncidents.add(d7);
        detailIncidents.add(d8);
        /*
        System.out.println(" - Type 1: " + d1.size() + " incidents.");
        if(d1.size()>0)
        {
            System.out.println("    · Biggest item size: " + d1.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d1.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 2: " + d2.size() + " incidents.");
        if(d2.size()>0)
        {
            System.out.println("    · Biggest item size: " + d2.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d2.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 3: " + d3.size() + " incidents.");
        if(d3.size()>0)
        {
            System.out.println("    · Biggest item size: " + d3.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d3.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 4: " + d4.size() + " incidents.");
        if(d4.size()>0)
        {
            System.out.println("    · Biggest item size: " + d4.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d4.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 5: " + d5.size() + " incidents.");
        if(d5.size()>0)
        {
            System.out.println("    · Biggest item size: " + d5.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d5.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 6: " + d6.size() + " incidents.");
        if(d6.size()>0)
        {
            System.out.println("    · Biggest item size: " + d6.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d6.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 7: " + d7.size() + " incidents.");
        if(d7.size()>0)
        {        
            System.out.println("    · Biggest item size: " + d7.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d7.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        System.out.println(" - Type 8: " + d8.size() + " incidents.");
        if(d8.size()>0)
        {        
            System.out.println("    · Biggest item size: " + d8.stream().mapToInt(List::size).max().getAsInt());
            System.out.println("    · Incident name: " + d8.stream().max(Comparator.comparing(List::size)).get().get(0).getDs_name());
        }
        
        System.out.println();
        int total = d1.size() + d2.size() + d3.size() + d4.size() + d5.size() + d6.size() + d7.size() + d8.size();
        System.out.println("Total: " + total + " incidents");
        System.out.println("----------------------------------------");
        */
        return detailIncidents;
    }

    private static String writeXLSIncidentsFile(String path, List<Incident> incidents) throws IOException
    {
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet s = wb.createSheet("Incidents");
        
        Date date = new Date(System.currentTimeMillis());
        
        File directory = new File(path+"Output/Incidents/");
        if (!directory.exists()) directory.mkdirs();
        String filename = path + "Output/Incidents/" + String.valueOf(date.toInstant().toEpochMilli()) + ".xls";
        
        // Create heading
        Row heading = s.createRow(0);
        heading.createCell(0).setCellValue("Filename");
        heading.createCell(1).setCellValue("Key");
        heading.createCell(2).setCellValue("Latitude");   
        heading.createCell(3).setCellValue("Longitude");
        heading.createCell(4).setCellValue("Timestamp");
        heading.createCell(5).setCellValue("Bike");
        heading.createCell(6).setCellValue("ChildCheckBox");
        heading.createCell(7).setCellValue("TrailerCheckBox");
        heading.createCell(8).setCellValue("pLoc");
        heading.createCell(9).setCellValue("Incident");
        heading.createCell(10).setCellValue("i1");
        heading.createCell(11).setCellValue("i2");
        heading.createCell(12).setCellValue("i3");
        heading.createCell(13).setCellValue("i4");
        heading.createCell(14).setCellValue("i5");
        heading.createCell(15).setCellValue("i6");
        heading.createCell(16).setCellValue("i7");
        heading.createCell(17).setCellValue("i8");
        heading.createCell(18).setCellValue("i9");
        heading.createCell(19).setCellValue("Scary");
        heading.createCell(20).setCellValue("Description");
        heading.createCell(21).setCellValue("i10");
        
        CellStyle styleTimestamp = wb.createCellStyle();
        HSSFDataFormat tf = wb.createDataFormat();
        styleTimestamp.setDataFormat(tf.getFormat("#####"));
        
        // Adding Data
        int r = 1;
        for (Incident i : incidents) 
        {
            Row row = s.createRow(r);
            // Ds_Name
            Cell cellDs_name = row.createCell(0);
            cellDs_name.setCellValue(i.getDs_name());
            // Key
            Cell cellKey = row.createCell(1);
            cellKey.setCellValue(i.getKey());
            // Latitude
            Cell cellLatitude = row.createCell(2);
            cellLatitude.setCellValue(i.getLatitude());
            // Longitude
            Cell cellLongitude = row.createCell(3);
            cellLongitude.setCellValue(i.getLongitude());
            // Timestamp
            Cell cellTimestamp = row.createCell(4);
            cellTimestamp.setCellValue(i.getTimestamp());
            cellTimestamp.setCellStyle(styleTimestamp); //Style
            // Bike
            Cell cellBike = row.createCell(5);
            cellBike.setCellValue(i.getBike());
            // ChildCheckBox
            Cell cellChildCheckBox = row.createCell(6);
            cellChildCheckBox.setCellValue(i.isChildCheckBox());            
            // ChildCheckBox
            Cell cellTrailerCheckBox = row.createCell(7);
            cellTrailerCheckBox.setCellValue(i.isTrailerCheckBox()); 
            // PLoc
            Cell cellpLoc = row.createCell(8);
            cellpLoc.setCellValue(i.getpLoc()); 
            // Incident
            Cell cellIncident = row.createCell(9);
            cellIncident.setCellValue(i.getIncident());  
            // I1
            Cell cellI1 = row.createCell(10);
            cellI1.setCellValue(i.isI1());
            // I2
            Cell cellI2 = row.createCell(11);
            cellI2.setCellValue(i.isI2());
            // I3
            Cell cellI3 = row.createCell(12);
            cellI3.setCellValue(i.isI3());
            // I4
            Cell cellI4 = row.createCell(13);
            cellI4.setCellValue(i.isI4());
            // I5
            Cell cellI5 = row.createCell(14);
            cellI5.setCellValue(i.isI5());
            // I6
            Cell cellI6 = row.createCell(15);
            cellI6.setCellValue(i.isI6());
            // I7
            Cell cellI7 = row.createCell(16);
            cellI7.setCellValue(i.isI7());
            // I8
            Cell cellI8 = row.createCell(17);
            cellI8.setCellValue(i.isI8());
            // I9
            Cell cellI9 = row.createCell(18);
            cellI9.setCellValue(i.isI9());            
            // Scary
            Cell cellScary = row.createCell(19);
            cellScary.setCellValue(i.isScary()); 
            // Description
            Cell cellDesc = row.createCell(20);
            cellDesc.setCellValue(i.getDesc());
            // I10
            Cell cellI10 = row.createCell(21);
            cellI10.setCellValue(i.isI10());   
            r++;
        }
        
        //Filter
        s.setAutoFilter(new CellRangeAddress(0, 0, 0, 21));
        s.createFreezePane(0, 1);

        //Autofit
        for(int k=0; k<=12; k++)
            s.autoSizeColumn(k);
        
        // Save file
        FileOutputStream out = new FileOutputStream(filename);
        wb.write(out);
        out.close();
        wb.close();
        
        return filename;

    }

    private static String writeXLSDetailFile(String path, ArrayList<List<List<IncidentDetailPhoneLoc_BikeType>>> incidentsDetail) throws IOException
    {
        Date date = new Date(System.currentTimeMillis());
        File directory = new File(path+"Output/IncidentsDetail/");
        if (!directory.exists()) directory.mkdirs();

        String filename = path + "Output/IncidentsDetail/WithMetadata-" + String.valueOf(date.toInstant().toEpochMilli()) + ".xls";
        
        HSSFWorkbook wb = new HSSFWorkbook();
        
        int type=1;
        for (List<List<IncidentDetailPhoneLoc_BikeType>> idtype : incidentsDetail)
        {
            HSSFSheet s = wb.createSheet("Type " + String.valueOf(type));

            // Create heading
            Row heading = s.createRow(0);
            heading.createCell(0).setCellValue("DS_Name");
            heading.createCell(1).setCellValue("Key");
            heading.createCell(2).setCellValue("Type");
            heading.createCell(3).setCellValue("PhoneLocation");
            heading.createCell(4).setCellValue("BikeType");
            heading.createCell(5).setCellValue("Latitude");
            heading.createCell(6).setCellValue("Longitude");
            heading.createCell(7).setCellValue("Acc_X");
            heading.createCell(8).setCellValue("Acc_Y");
            heading.createCell(9).setCellValue("Acc_z");
            heading.createCell(10).setCellValue("Timestamp");
            heading.createCell(11).setCellValue("Acc_68");
            heading.createCell(12).setCellValue("Gyr_a");
            heading.createCell(13).setCellValue("Gyr_b");
            heading.createCell(14).setCellValue("Gyr_c");

            CellStyle styleTimestamp = wb.createCellStyle();
            HSSFDataFormat tf = wb.createDataFormat();
            styleTimestamp.setDataFormat(tf.getFormat("#####"));

            // Adding Data
            int r = 1, id = 1;
            for (List<IncidentDetailPhoneLoc_BikeType> lid : idtype) 
            {
                for (IncidentDetailPhoneLoc_BikeType i : lid)
                {
                    Row row = s.createRow(r);
                    // Ds_Name
                    Cell cellDs_name = row.createCell(0);
                    cellDs_name.setCellValue(i.getDs_name());
                    // Key
                    Cell cellKey = row.createCell(1);
                    cellKey.setCellValue(id);
                    // Type
                    Cell cellType = row.createCell(2);
                    cellType.setCellValue(i.getType());
                    // PhoneLocation
                    Cell cellpLoc = row.createCell(3);
                    cellpLoc.setCellValue(i.getpLoc());
                    // BikeType
                    Cell cellBikeType = row.createCell(4);
                    cellBikeType.setCellValue(i.getBikeType());                                        
                    // Latitude
                    Cell cellLatitude = row.createCell(5);
                    cellLatitude.setCellValue(i.getLatitude());
                    // Longitude
                    Cell cellLongitude = row.createCell(6);
                    cellLongitude.setCellValue(i.getLongitude());
                    // Acc_x
                    Cell cellAcc_x = row.createCell(7);
                    cellAcc_x.setCellValue(i.getAcc_x());
                    // Acc_y
                    Cell cellAcc_y = row.createCell(8);
                    cellAcc_y.setCellValue(i.getAcc_y());
                    // Acc_z
                    Cell cellAcc_z = row.createCell(9);
                    cellAcc_z.setCellValue(i.getAcc_z());
                    // Timestamp
                    Cell cellTimestamp = row.createCell(10);
                    cellTimestamp.setCellValue(i.getTimestamp());
                    cellTimestamp.setCellStyle(styleTimestamp); //Style
                    // Acc_68
                    Cell cellAcc_68 = row.createCell(11);
                    cellAcc_68.setCellValue(i.getAcc_68());
                    // Gyr_A
                    Cell cellgyr_A = row.createCell(12);
                    cellgyr_A.setCellValue(i.getGyr_a());
                    // Gyr_B
                    Cell cellgyr_B = row.createCell(13);
                    cellgyr_B.setCellValue(i.getGyr_b());
                    // Gyr_C
                    Cell cellgyr_C = row.createCell(14);
                    cellgyr_C.setCellValue(i.getGyr_c());
                    r++;
                }
                id++;
            }
            
            //Filter
            s.setAutoFilter(new CellRangeAddress(0, 0, 0, 12));
            s.createFreezePane(0, 1);
            
            //Autofit
            for(int k=0; k<=12; k++)
                s.autoSizeColumn(k);
            
            type++;
        }
        
        
        // Save file
        FileOutputStream out = new FileOutputStream(filename);
        wb.write(out);
        out.close();
        wb.close();
        
        return filename;

    }    
    
    public static String writeCSVDetailFile(String path, ArrayList<List<List<IncidentDetailPhoneLoc_BikeType>>> incidentsDetail) throws IOException
    {
        Date date = new Date(System.currentTimeMillis());
        File directory = new File(path+"Output/IncidentsDetail/");
        if (!directory.exists()) directory.mkdirs();

        String filename = path + "Output/IncidentsDetail/" + String.valueOf(date.toInstant().toEpochMilli()) + ".csv";
        String line = "";
        int csvRecords = 0;
                
        FileWriter writer = new FileWriter(filename);
        
        line = "DS_Name;Key;Type;PhoneLocation;BikeType;Latitude;Longitude;Acc_X;Acc_Y;Acc_z;Timestamp;Acc_68;Gyr_a;Gyr_b;Gyr_c\n";
        writer.append(line);
        
        for(List<List<IncidentDetailPhoneLoc_BikeType>> l1 : incidentsDetail)
        {
            for(List<IncidentDetailPhoneLoc_BikeType> l2 : l1)
            {
                for(IncidentDetailPhoneLoc_BikeType l : l2)
                {
                    line  = l.getDs_name() + ";";
                    line += l.getKey() + ";";
                    line += l.getType() + ";";
                    line += l.getpLoc() + ";";
                    line += l.getBikeType() + ";";
                    line += l.getLatitude() + ";";
                    line += l.getLongitude() + ";";
                    line += l.getAcc_x() + ";";
                    line += l.getAcc_y() + ";";
                    line += l.getAcc_z() + ";";
                    line += l.getTimestamp() + ";";
                    line += l.getAcc_68() + ";";
                    line += l.getGyr_a() + ";";
                    line += l.getGyr_b() + ";";
                    line += l.getGyr_c() + "\n";

                    writer.append(line);        
                    csvRecords++;

                    if(csvRecords%1000==0) System.out.println(csvRecords + " csv records added");
                }
            } 
        }
        
        
        writer.flush();
        writer.close();
        System.out.println(String.format("Csv records: %d",csvRecords));
        return filename;
    }
}
