package org.stello.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreateReportsUtil {

    private static void main(String[] args) throws IOException {
//        createReportsCeoToVp();
        Map<String, List<String>> reportsMap1 = getReportsMap("src/main/resources/JsonRequests/CreateCeoAndCto.json");
        Map<String, List<String>> reportsMap2 = getReportsMap("src/main/resources/JsonRequests/Create1VpForEveryDepartment.json");
        Map<String, List<String>> reportsMap3 = getReportsMap("src/main/resources/JsonRequests/Create3DirectorsEveryDepartment.json");
        Map<String, List<String>> reportsMap4 = getReportsMap("src/main/resources/JsonRequests/Create5SeniorManagerEveryDepartment.json");
        Map<String, List<String>> reportsMap5 = getReportsMap("src/main/resources/JsonRequests/Create10ManagersEvertDepartment.json");
        Map<String, List<String>> reportsMap6 = getReportsMap("src/main/resources/JsonRequests/Create20LeadsEveryDepartment.json");
        Map<String, List<String>> reportsMap7 = getReportsMap("src/main/resources/JsonRequests/Create500Employees.json");
        print(reportsMap6);
//        addReport(reportsMap1, reportsMap2);
        addReport(reportsMap2, reportsMap3);
        addReport(reportsMap3, reportsMap4);
        addReport(reportsMap4, reportsMap5);
        addReport(reportsMap5, reportsMap6);
        addReport(reportsMap6, reportsMap7);
    }

    private static void addReport(Map<String, List<String>> reportsMap1, Map<String, List<String>> reportsMap2) {
        Random random = new Random();
        for(String key : reportsMap2.keySet()){
            for(String empId : reportsMap2.get(key)){
                int idx = random.nextInt(0, reportsMap1.get(key).size());
                System.out.println("{\n"+"\tempId: "+empId+",\n\treportsTo: "+reportsMap1.get(key).get(idx)+"\n}");

            }
        }
    }

    private static void print(Map<String, List<String>> map){
        for(String key : map.keySet()){
            System.out.println(key+"->" + map.get(key));
        }
        System.out.println();
    }

    public static Map<String, List<String>> getReportsMap(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        Map<String, List<String>> reportsMap= new HashMap<>();
        String empId = null;
        String dept = null;
        while(line != null){
            if(line.contains("empId")){
                empId = line.substring(13, line.length()-1);
            }
            if(line.contains("department")){
                dept = line.substring(19, line.lastIndexOf("\""));
            }
            if(line.contains("currency")){
                if(!reportsMap.containsKey(dept)){
                    reportsMap.put(dept, new ArrayList<>());
                }
                reportsMap.get(dept).add(empId);
            }
            line = br.readLine();
        }
        return reportsMap;
    }
}
