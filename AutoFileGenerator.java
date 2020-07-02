package main;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AutoFileGenerator {


    public static void main(String args[]) throws IOException {

        String fileName = "resources/cust_xml_datamaps.dat";
        ClassLoader classLoader = new AutoFileGenerator().getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        ArrayList < ArrayList < String > > outputSet = new ArrayList < ArrayList < String > > ();
        Map < String, Integer > mapN2V = new HashMap < String, Integer > ();
        Map < Integer, String > mapV2N = new HashMap < Integer, String > ();
        long start = System.currentTimeMillis();

        initiallise(file, outputSet, mapN2V, mapV2N);
        populateAddmemberValueMap(file, outputSet, mapN2V);
        populateValueMapAndDataMap(file, outputSet, mapN2V);
        createDataMapFiles(outputSet, mapV2N);
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
            while ((line = br.readLine()) != null && !line.equals("%")) {
                countOfLinesInOriginalFile++;
            }
        }

        for (int i = 0; i < outputSet.size(); i++) {
            for (String str: outputSet.get(i)) {
                countOfLinesInGeneratedFiles++;
                if (RegexMatcher.isMatchFound("\n", str)) {
                    countOfLinesInGeneratedFiles += RegexMatcher.getCountofOccurence("\n", str);
                }
            }
        }


        if (countOfLinesInOriginalFile == countOfLinesInGeneratedFiles) {
            System.out.println("Data Validation completed : NO issue found");
        } else {
            System.out.println("Issue found in data validation : No. of lines in original file is " +
                countOfLinesInOriginalFile + " And  No. of lines in generated file is" + countOfLinesInGeneratedFiles);
        }

    }


    private static void populateValueMapAndDataMap(File file,
        ArrayList < ArrayList < String >> outputSet, Map < String, Integer > mapN2V) throws IOException {

        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("ValueMap:") || line.startsWith("DataMap:")) {
                    if (line.charAt(line.length() - 1) != ';') {
                        String addLine = br.readLine();
                        while (addLine.charAt(addLine.length() - 1) != ';') {
                            line += "\n" + addLine;
                            addLine = br.readLine();
                        }
                        line += "\n" + addLine;
                    }
                    String dataMapN = "";
                    if (line.startsWith("DataMap:")) {
                        String regexToFilterDataMapComponent = "^(DataMap:\").*?(\")";
                        String FilteredDataMapComponent = RegexMatcher.getString(regexToFilterDataMapComponent, line);
                        String pattern = "(?<=\").*(?=\")";
                        dataMapN = RegexMatcher.getString(pattern, FilteredDataMapComponent);
                    } else if (line.startsWith("ValueMap:")) {
                        String regexToFilterValueComponent = "^(ValueMap:\").*?(\")";
                        String FilteredValueComponent = RegexMatcher.getString(regexToFilterValueComponent, line);
                        String pattern = "(?<=\").*(?=_[0-9]+\")";
                        dataMapN = RegexMatcher.getString(pattern, FilteredValueComponent);
                    }

                    int index = mapN2V.get(dataMapN);
                    outputSet.get(index).add(line);
                    count++;
                }
            }
        }
    }


    private static void populateAddmemberValueMap(File file,
        ArrayList < ArrayList < String >> outputSet, Map < String, Integer > mapN2V) throws IOException {
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("addmember ValueMap,")) {
                    if (line.charAt(line.length() - 1) != '.') {
                        String addLine = br.readLine();
                        line += "\n" + addLine;
                    }
                    String pattern = "(?<=\").*(?=\")";
                    String dataMapValueName = RegexMatcher.getString(pattern, line);
                    String patternTogetDataNameFromValueMap = ".*(?=_[0-9]+)";
                    String dataMapN = RegexMatcher.getString(patternTogetDataNameFromValueMap, dataMapValueName);
                    int index = mapN2V.get(dataMapN);
                    outputSet.get(index).add(line);
                }
            }
        }
    }


    private static void createDataMapFiles(
        ArrayList < ArrayList < String >> outputSet, Map < Integer, String > mapV2N) {
        for (int i = 0; i < outputSet.size(); i++) {
            String outputFileName = mapV2N.get(i);
            try (FileWriter myWriter = new FileWriter("src/outputs/" + outputFileName + ".txt")) {
                for (String str: outputSet.get(i)) {
                    myWriter.write(str + "\n");
                }
                myWriter.write("%");
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
                if (line.contains("addmember DataMap,")) {
                    if (line.charAt(line.length() - 1) != '.') {
                        String addLine = br.readLine();
                        line += "\n" + addLine;
                    }
                    String pattern = "(?<=\").*(?=\")";
                    String dataMapName = RegexMatcher.getString(pattern, line);
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