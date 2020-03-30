# ExtractData

This application will extract incidents from a dataset that uses SimRa's project dataset format (https://github.com/simra-project/dataset)

In order to run it, you must change the value of String variable 'folder' located in ExtractData class, to point to your dataset folder.

The result of the program will be in a folder called 'Output' inside the defined folder path. Inside 'Output' folder there will be two more folders called 'Incidents' and IncidentsDetail'
- Incidents: An excel will be generated with all the annotation of the incidents found in the dataset
- Incidents Detail: An excel will be generated with the incident signals. There will be 8 worksheets, one for each type of incident, and inside there are all the incidents which can be distinguished by the ‘Key’ value.

NOTE: If the dataset size is too big, you must split the dataset into different folders and run the program for each of the folders due to excel cells limits. Other option is to comment method writeXLSDetailFile() in main() and uncomment method writeCSVDetailFile(). In such case, a '.csv' file will be generated instead of Incidents Detail excel.
