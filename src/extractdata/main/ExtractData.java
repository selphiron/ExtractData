/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractdata.main;

import extractdata.entities.*;
import extractdata.dao.*;
import java.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.stream.Collectors;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;

//import org.apache.poi.ss.usermodel.XSSFWorkbook;

/**
 *
 * @author AlbertSanchez
 */
public class ExtractData {

    /**
     * @param args the command line arguments
     */        
    
    // Excel Extraction
    private static boolean EXTRACTION = true;
    
    // Folder path
    //private static String folder = "/Users/AlbertSanchez/Desktop/TFM/GitHub/dataset/Berlin/Rides/Test/";
    private static String folder = "/Users/AlbertSanchez/Desktop/TFM/GitHub/dataset/Berlin/Rides/06_01_19_to_09_29_19/";

    //Time interval for incidents
    static final int TS_TO_S = 3037; //3037 equals 1 second in timestamp period
    private static int dt = 5*TS_TO_S; //In s.; The total time interval will be 10. (<--5-- center t --5-->)
    
    //Time interval in GPS Coordinates
    //private static final int dCoord = 1;
    
    public static void main(String[] args) throws IOException 
    {   
        System.out.println("Begining the Data Extraction");
        System.out.println("...");
        System.out.println("Searching files in " + folder + " ...");
        
        List<String> fileNames = new ArrayList<String>();
        fileNames = getFileNames(folder);
        
        if (fileNames.isEmpty())
            System.out.println("0 files founded");
        else
            System.out.println(fileNames.size() + " files found");
        
        System.out.println("Reading files...");
        
        List<Incident> incidents = new ArrayList<>();
        
        for (String file : fileNames)
        {
            List<Incident> i = readIncidents(file);
            incidents.addAll(i);
        }
        System.out.println("Files readed. " + incidents.size() + " incidents found");
        
        ArrayList<List<List<IncidentDetail>>> detailIncidents = new ArrayList<>();
        
        // Get incidents (filename, type, timestamp)
        List<IncidentCoordinates> tempIncidents = new ArrayList<>();
        for (Incident i : incidents)
        {
            if (i.getIncident() != 0)
            {
                IncidentCoordinates iT = new IncidentCoordinates();
                iT.setDs_name(i.getDs_name());
                iT.setIncident(i.getIncident());
                iT.setLatitude(i.getLatitude());
                iT.setLongitude(i.getLongitude());
                tempIncidents.add(iT);
            }
        }

        detailIncidents = readDetail(tempIncidents);
        
        if (EXTRACTION)
        {
            System.out.println("Generating XLS Incidents file");
            String xlsName = writeXLSIncidentsFile(folder, incidents);
            System.out.println("XLS file generated. Name: " + xlsName);
        
            System.out.println("Generating XLS Detail file");
            String xlsDetailName = writeXLSDetailFile(folder, detailIncidents);
            System.out.println("XLS file generated. Name: " + xlsDetailName);
        }
    }
    
    private static List<String> getFileNames(String directory)
    {
        List<String> results = new ArrayList<String>();
        File[] files = new File(directory).listFiles();

        for (File file : files) 
        {
            if (file.isFile()) 
                if(!file.getName().startsWith("."))
                    results.add(directory + file.getName());
        }
        return results;
    }
    
    private static List<Incident> readIncidents(String file) throws IOException
    {   
        List<Incident> incidents = new ArrayList<>();
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String line;
        
        String[] s = file.split("/");
        String fileName = s[s.length-1];

        line = br.readLine(); //Read the 1st line which is <app version>#<file version>
        line = br.readLine(); //Read the 2nd line which are the headers
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
            
            String description = incidentFields[19];
            if(incidentFields.length > 20)
                for(int i=20; i<incidentFields.length; i++)
                    description = description + incidentFields[i];
            
            incident.setDesc(description);

            if (incident.getIncident() != 0)
                incidents.add(incident);
            line = br.readLine();
        }    
        return incidents;
    }
   
    private static ArrayList<List<List<IncidentDetail>>> readDetail(List<IncidentCoordinates> incidents) throws IOException
    {
        ArrayList<List<List<IncidentDetail>>> detailIncidents = new ArrayList<>();
        List<List<IncidentDetail>> d1 = new ArrayList<>();
        List<List<IncidentDetail>> d2 = new ArrayList<>();
        List<List<IncidentDetail>> d3 = new ArrayList<>();
        List<List<IncidentDetail>> d4 = new ArrayList<>();
        List<List<IncidentDetail>> d5 = new ArrayList<>();
        List<List<IncidentDetail>> d6 = new ArrayList<>();
        List<List<IncidentDetail>> d7 = new ArrayList<>();
        List<List<IncidentDetail>> d8 = new ArrayList<>();
        double prevLatDetailIncident=0;
        double prevLonDetailIncident=0;
        float prevAcc_68DetailIncident=0;
        float prevGyr_aDetailIncident=0;
        float prevGyr_bDetailIncident=0;
        float prevGyr_cDetailIncident=0; 
        int i=-1;

        for(IncidentCoordinates iT : incidents)
        {
            i++;
            List<IncidentDetail> idet = new ArrayList<>();
            IncidentDetail detailIncident = null;
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
            }
            
            line = lnr.readLine();
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
             
                detailIncident = new IncidentDetail();

                detailIncident.setDs_name(iT.getDs_name());
                detailIncident.setKey(i);
                detailIncident.setType(iT.getIncident());

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
            detailIncident = new IncidentDetail();

            detailIncident.setDs_name(iT.getDs_name());
            detailIncident.setKey(i);
            detailIncident.setType(iT.getIncident());

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
                detailIncident = new IncidentDetail();

                detailIncident.setDs_name(iT.getDs_name());
                detailIncident.setKey(i);
                detailIncident.setType(iT.getIncident());

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
            detailIncident = new IncidentDetail();

            detailIncident.setDs_name(iT.getDs_name());
            detailIncident.setKey(i);
            detailIncident.setType(iT.getIncident());

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

            
            if (!idet.isEmpty())
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
                System.out.println("Added incident " + i);
            }
            
        }
        detailIncidents.add(d1);
        detailIncidents.add(d2);
        detailIncidents.add(d3);
        detailIncidents.add(d4);
        detailIncidents.add(d5);
        detailIncidents.add(d6);
        detailIncidents.add(d7);
        detailIncidents.add(d8);
        
        System.out.println("----------------------------------------");
        System.out.println("Summary of incidents added:");
        System.out.println(" - Type 1: " + d1.size() + " incidents");
        System.out.println(" - Type 2: " + d2.size() + " incidents");
        System.out.println(" - Type 3: " + d3.size() + " incidents");
        System.out.println(" - Type 4: " + d4.size() + " incidents");
        System.out.println(" - Type 5: " + d5.size() + " incidents");
        System.out.println(" - Type 6: " + d6.size() + " incidents");
        System.out.println(" - Type 7: " + d7.size() + " incidents");
        System.out.println(" - Type 8: " + d8.size() + " incidents");
        System.out.println();
        int total = d1.size() + d2.size() + d3.size() + d4.size() + d5.size() + d6.size() + d7.size() + d8.size();
        System.out.println("Total: " + total + " incidents");
        System.out.println("----------------------------------------");
        
        return detailIncidents;
    }

    private static String writeXLSIncidentsFile(String path, List<Incident> incidents) throws IOException
    {
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet s = wb.createSheet("Incidents");
        
        Date date = new Date(System.currentTimeMillis());
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
            r++;
        }
        
        //Filter
        s.setAutoFilter(new CellRangeAddress(0, 0, 0, 12));
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
    
    private static String writeXLSDetailFile(String path, ArrayList<List<List<IncidentDetail>>> incidentsDetail) throws IOException
    {
        Date date = new Date(System.currentTimeMillis());
        String filename = path + "Output/IncidentsDetail/" + String.valueOf(date.toInstant().toEpochMilli()) + ".xls";
        
        HSSFWorkbook wb = new HSSFWorkbook();
        
        int type=1;
        for (List<List<IncidentDetail>> idtype : incidentsDetail)
        {
            HSSFSheet s = wb.createSheet("Type " + String.valueOf(type));

            // Create heading
            Row heading = s.createRow(0);
            heading.createCell(0).setCellValue("DS_Name");
            heading.createCell(1).setCellValue("Key");
            heading.createCell(2).setCellValue("Type");
            heading.createCell(3).setCellValue("Latitude");
            heading.createCell(4).setCellValue("Longitude");
            heading.createCell(5).setCellValue("Acc_X");
            heading.createCell(6).setCellValue("Acc_Y");
            heading.createCell(7).setCellValue("Acc_z");
            heading.createCell(8).setCellValue("Timestamp");
            heading.createCell(9).setCellValue("Acc_68");
            heading.createCell(10).setCellValue("Gyr_a");
            heading.createCell(11).setCellValue("Gyr_b");
            heading.createCell(12).setCellValue("Gyr_c");

            CellStyle styleTimestamp = wb.createCellStyle();
            HSSFDataFormat tf = wb.createDataFormat();
            styleTimestamp.setDataFormat(tf.getFormat("#####"));

            // Adding Data
            int r = 1, id = 1;
            for (List<IncidentDetail> lid : idtype) 
            {
                for (IncidentDetail i : lid)
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
                    // Latitude
                    Cell cellLatitude = row.createCell(3);
                    cellLatitude.setCellValue(i.getLatitude());
                    // Longitude
                    Cell cellLongitude = row.createCell(4);
                    cellLongitude.setCellValue(i.getLongitude());
                    // Acc_x
                    Cell cellAcc_x = row.createCell(5);
                    cellAcc_x.setCellValue(i.getAcc_x());
                    // Acc_y
                    Cell cellAcc_y = row.createCell(6);
                    cellAcc_y.setCellValue(i.getAcc_y());
                    // Acc_z
                    Cell cellAcc_z = row.createCell(7);
                    cellAcc_z.setCellValue(i.getAcc_z());
                    // Timestamp
                    Cell cellTimestamp = row.createCell(8);
                    cellTimestamp.setCellValue(i.getTimestamp());
                    cellTimestamp.setCellStyle(styleTimestamp); //Style
                    // Acc_68
                    Cell cellAcc_68 = row.createCell(9);
                    cellAcc_68.setCellValue(i.getAcc_68());
                    // Gyr_A
                    Cell cellgyr_A = row.createCell(10);
                    cellgyr_A.setCellValue(i.getGyr_a());
                    // Gyr_B
                    Cell cellgyr_B = row.createCell(11);
                    cellgyr_B.setCellValue(i.getGyr_b());
                    // Gyr_C
                    Cell cellgyr_C = row.createCell(12);
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

}
