package main;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AutoFileGenerator {


    public static void main(String args[]) throws IOException {

        String sourceFileName = "resources/cust_xml_datamaps.dat";
        ClassLoader classLoader = new AutoFileGenerator().getClass().getClassLoader();
        File file = new File(classLoader.getResource(sourceFileName).getFile());
        ArrayList < ArrayList < String > > outputSet = new ArrayList < ArrayList < String > > ();
        Map < String, Integer > mapN2V = new HashMap < String, Integer > ();
        Map < Integer, String > mapV2N = new HashMap < Integer, String > ();
        String outputFileExtension =".txt";
        long start = System.currentTimeMillis();

        initiallise(file, outputSet, mapN2V, mapV2N);
        populateAddmemberValueMap(file, outputSet, mapN2V);
        populateValueMapAndDataMap(file, outputSet, mapN2V);
        createDataMapFiles(outputSet, mapV2N, outputFileExtension);
        validateData(outputSet, file);

        long end = System.currentTimeMillis();
        System.out.println("Time Taken : " + (end - start) + "ms");

    }


    private static void validateData(ArrayList < ArrayList < String >> outputSet,
        File file) throws IOException {

        int countOfLinesInOriginalFile = 0;
        int countOfLinesInGeneratedFiles = 0;
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null && !line.equals(Constants.EndOfFileCharacter)) {
                countOfLinesInOriginalFile++;
            }
        }

        for (int i = 0; i < outputSet.size(); i++) {
            for (String str: outputSet.get(i)) {
                countOfLinesInGeneratedFiles++;
                if (RegexMatcher.isMatchFound(Constants.NewLine, str)) {
                    countOfLinesInGeneratedFiles += RegexMatcher.getCountofOccurence(Constants.NewLine, str);
                }
            }
        }


        if (countOfLinesInOriginalFile == countOfLinesInGeneratedFiles) {
            System.out.println("Data Validation completed successfully : NO issue found");
        } else {
            System.out.println("Data Validation Failed : No. of lines in original file is : " +
                countOfLinesInOriginalFile + " And  Sum of lines of All generated file is : " + countOfLinesInGeneratedFiles);
        }

    }


    private static void populateValueMapAndDataMap(File file,
        ArrayList < ArrayList < String >> outputSet, Map < String, Integer > mapN2V) throws IOException {

        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith(Constants.ValueMap) || line.startsWith(Constants.DataMap)) {
                    if (line.charAt(line.length() - 1) != ';') {
                        String addLine = br.readLine();
                        while (addLine.charAt(addLine.length() - 1) != ';') {
                            line += Constants.NewLine + addLine;
                            addLine = br.readLine();
                        }
                        line += Constants.NewLine + addLine;
                    }
                    String dataMapN = "";
                    if (line.startsWith(Constants.DataMap)) {
                        String FilteredDataMapComponent = RegexMatcher.getString(Constants.FilterDataMapComponent, line);
                        dataMapN = RegexMatcher.getString(Constants.GrepQuotationContent, FilteredDataMapComponent);
                    } else if (line.startsWith(Constants.ValueMap)) {
                        String FilteredValueComponent = RegexMatcher.getString(Constants.FilterValueMapComponent, line);
                        String GetDataInsideQuotation = RegexMatcher.getString(Constants.GrepQuotationContent, FilteredValueComponent);
                        dataMapN = RegexMatcher.getString(Constants.GrepQuotationContentFromValueMap, GetDataInsideQuotation);
                    }
                    try{
                    int index = mapN2V.get(dataMapN);
                    outputSet.get(index).add(line);
                    }
                    catch(Exception ex){
                    	System.out.println("\"addmember DataMap\" entrie for DataMap ** "+dataMapN+" ** is missing.");
                    }
                }
            }
        }
    }


    private static void populateAddmemberValueMap(File file,
        ArrayList < ArrayList < String >> outputSet, Map < String, Integer > mapN2V) throws IOException {
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(Constants.AddMemberValueMap)) {
                    if (line.charAt(line.length() - 1) != '.') {
                        String addLine = br.readLine();
                        line += Constants.NewLine + addLine;
                    }
                    String dataMapValueName = RegexMatcher.getString(Constants.GrepQuotationContent, line);
                    String dataMapN = RegexMatcher.getString(Constants.GrepQuotationContentFromValueMap, dataMapValueName);
                    int index = mapN2V.get(dataMapN);
                    outputSet.get(index).add(line);
                }
            }
        }
    }


    private static void createDataMapFiles(
        ArrayList < ArrayList < String >> outputSet, Map < Integer, String > mapV2N , String fileExtension) {
        for (int i = 0; i < outputSet.size(); i++) {
            String outputFileName = mapV2N.get(i);
            try (FileWriter myWriter = new FileWriter("src/outputs/" + outputFileName + fileExtension)) {
                for (String str: outputSet.get(i)) {
                    myWriter.write(str + Constants.NewLine);
                }
                myWriter.write(Constants.EndOfFileCharacter);
                myWriter.close();
                
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }


    public static void initiallise(File file, ArrayList < ArrayList < String > > outputSet,
        Map < String, Integer > mapN2V, Map < Integer, String > mapV2N) throws IOException {
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.contains(Constants.AddMemberDataMap)) {
                    if (line.charAt(line.length() - 1) != '.') {
                        String addLine = br.readLine();
                        line += Constants.NewLine + addLine;
                    }
                    String dataMapName = RegexMatcher.getString(Constants.GrepQuotationContent, line);
                    ArrayList < String > newData = new ArrayList();
                    newData.add(line);
                    outputSet.add(newData);
                    mapN2V.put(dataMapName, count);
                    mapV2N.put(count, dataMapName);
                    count++;
                }
            }
            System.out.println("count of dataMaps to be created :" + count);
        }
    }
}