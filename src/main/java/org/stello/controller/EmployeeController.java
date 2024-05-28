package org.stello.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stello.model.AddReportsRequest;
import org.stello.model.CommonManagerRequest;
import org.stello.model.Employee;
import org.stello.model.EmployeeWithMaxReports;
import org.stello.model.Salary;
import org.stello.model.SimpleResponse;
import org.stello.model.dataInitialization.BulkEmployeeList;
import org.stello.model.dataInitialization.BulkReportsToList;
import org.stello.repository.EmployeesDataRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    /**
     * Created all logic inside the below repository. We can add one more layer of abstraction i.e. Service Classes between Controller and Repository.
     * */
    @Autowired
    EmployeesDataRepository employeesDataRepository;

    @PostConstruct
    public void initializeData() throws IOException {
        //Initializing data to have ~800 employees already created for testing APIs at the time of Application Start.

        Gson gson = new Gson();
        String jsonString1 = getJsonString("src/main/resources/JsonRequests/dataInitialization/CreateCeoAndCto.json");
        BulkEmployeeList employeeList1 = gson.fromJson(jsonString1, BulkEmployeeList.class);
        this.createEmployee(employeeList1.getEmployees());

        String jsonString2 = getJsonString("src/main/resources/JsonRequests/dataInitialization/Create1VpForEveryDepartment.json");
        BulkEmployeeList employeeList2 = gson.fromJson(jsonString2, BulkEmployeeList.class);
        this.createEmployee(employeeList2.getEmployees());

        String jsonString3 = getJsonString("src/main/resources/JsonRequests/dataInitialization/Create3DirectorsEveryDepartment.json");
        BulkEmployeeList employeeList3 = gson.fromJson(jsonString3, BulkEmployeeList.class);
        this.createEmployee(employeeList3.getEmployees());

        String jsonString4 = getJsonString("src/main/resources/JsonRequests/dataInitialization/Create5SeniorManagerEveryDepartment.json");
        BulkEmployeeList employeeList4 = gson.fromJson(jsonString4, BulkEmployeeList.class);
        this.createEmployee(employeeList4.getEmployees());

        String jsonString5 = getJsonString("src/main/resources/JsonRequests/dataInitialization/Create10ManagersEvertDepartment.json");
        BulkEmployeeList employeeList5 = gson.fromJson(jsonString5, BulkEmployeeList.class);
        this.createEmployee(employeeList5.getEmployees());

        String jsonString6 = getJsonString("src/main/resources/JsonRequests/dataInitialization/Create20LeadsEveryDepartment.json");
        BulkEmployeeList employeeList6 = gson.fromJson(jsonString6, BulkEmployeeList.class);
        this.createEmployee(employeeList6.getEmployees());

        String jsonString7 = getJsonString("src/main/resources/JsonRequests/dataInitialization/Create500Employees.json");
        BulkEmployeeList employeeList7 = gson.fromJson(jsonString7, BulkEmployeeList.class);
        this.createEmployee(employeeList7.getEmployees());

        String jsonString8 = getJsonString("src/main/resources/JsonRequests/dataInitialization/createReportsTo.json");
        BulkReportsToList addReports = gson.fromJson(jsonString8, BulkReportsToList.class);
        this.addReports(addReports.getBulkReports());

    }

    private String getJsonString(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        while(line != null){
            sb.append(line+" ");
            line = br.readLine();
        }
        return sb.toString();
    }

    @GetMapping("/{id}")
    public SimpleResponse getDetails(@PathVariable String id){
        Employee result = employeesDataRepository.getEmployeeById(id);
        if(result == null){
            return  SimpleResponse.builder().status(400).message(String.format("Employee with empId {} does not exist", id)).build();
        }
        return  SimpleResponse.builder().status(200).data(result).build();
    }

    @PostMapping("/create")
    public SimpleResponse createEmployee(@RequestBody List<Employee> employeeList){
        SimpleResponse response = SimpleResponse.builder().status(200).build();
        StringBuilder sb = new StringBuilder("");
        int idx = 0;
        for(Employee employee : employeeList){
            SimpleResponse addEmployeeResponse = employeesDataRepository.addNewEmployee(employee.getName(),
                    employee.getEmpId(),
                    employee.getRole().toString(),
                    employee.getDepartment().toString(),
                    employee.getReportsTo(),
                    employee.getSalary().getSalary(),
                    employee.getSalary().getCurrency()
            );
            sb.append(idx++ +":" + addEmployeeResponse.getMessage());
        }
        response.setMessage(sb.toString());
        return response;
    }

    @PostMapping("/addReports")
    public SimpleResponse addReports(@RequestBody List<AddReportsRequest> input){
        SimpleResponse response = SimpleResponse.builder().status(200).build();
        StringBuilder sb = new StringBuilder("");
        int idx = 0;
        for(AddReportsRequest reportsToRequest : input){
            SimpleResponse addEmployeeResponse = employeesDataRepository.addReports(reportsToRequest.getEmpId(), reportsToRequest.getReportsTo());
            sb.append(idx++ +":" + addEmployeeResponse.getMessage()+", ");
        }
        response.setMessage(sb.toString());
        return response;
    }

    @GetMapping("/employeeWithMaximumReport")
    public SimpleResponse getEmployeeWithMaximumReport(){
        EmployeeWithMaxReports employeeWithMaxReports = employeesDataRepository.getEmployeeWithMaximumDirectReports();
        return SimpleResponse.builder().status(200).data(employeeWithMaxReports).message("The Employee with maximum Reports is").build();
    }

    @GetMapping("/findCommonManager")
    public SimpleResponse findCommonManager(@RequestBody CommonManagerRequest commonManagerRequest){
        return employeesDataRepository.findCommonManager(commonManagerRequest.getFirst(), commonManagerRequest.getSecond());
    }

    @GetMapping("/budget")
    public SimpleResponse getBudget(){
        return employeesDataRepository.getBudget();
    }

    @PutMapping("/updateSalary/{id}")
    public SimpleResponse updateSalary(@PathVariable String id, @RequestBody Salary salary){
        return employeesDataRepository.updateSalary(id, salary);
    }

}
