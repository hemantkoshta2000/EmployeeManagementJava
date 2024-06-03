package org.stello.repository;

import ch.qos.logback.core.util.StringCollectionUtil;
import org.springframework.util.StringUtils;
import org.stello.model.AllDepartmentBudget;
import org.stello.model.CommonManagerResponse;
import org.stello.model.Department;
import org.stello.model.Employee;
import org.stello.model.EmployeeWithMaxReports;
import org.stello.model.Role;
import org.stello.model.Salary;
import org.stello.model.SimpleResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeesDataRepository {
    /**
     * Ideally this should be stored in Database, but here I am creating it as in-memory data.
     * */
    private Map<String, Employee> globalEmployeeData;
    private static final String rootEmpId = "Company";

    /**
     * This map holds budget in USD.
     */
    private Map<Department, Double> departmentBudgetMap;
    public EmployeesDataRepository(){

        this.globalEmployeeData = new HashMap<>();
        this.departmentBudgetMap = new HashMap<>();
        for(Department d : Department.values()){
            this.departmentBudgetMap.put(d, 0.0D);
        }

        Employee company = Employee.builder().empId(rootEmpId)
                .role(Role.COMPANY)
                .name("Basic Company ltd.")
                .directReports(new ArrayList<>())
                .build();
        company.setReportsTo(rootEmpId);
        this.globalEmployeeData.put(rootEmpId, company);
    }

    public SimpleResponse addNewEmployee(String name, String empId, String role, String department, String reportsToEmpId, Double salary, String currency){
        try {
            if(this.globalEmployeeData.containsKey(empId)){
                throw new Exception(String.format("Employee with empId : %s already exists.", empId));
            }

            if(!this.globalEmployeeData.containsKey(reportsToEmpId)){
                throw new Exception(String.format("Employee with empId : %s does not exists to whom this employee was supposed to report", reportsToEmpId));
            }

            Employee newEmployee = Employee.builder()
                    .empId(empId)
                    .role(Role.valueOf(role))
                    .name(name)
                    .directReports(new ArrayList<>())
                    .department(Department.valueOf(department))
                    .salary(Salary.builder().salary(salary).currency(currency).build())
                    .build();

            this.globalEmployeeData.put(empId, newEmployee);
            addReports(empId, reportsToEmpId);

            this.departmentBudgetMap.put(Department.valueOf(department), this.departmentBudgetMap.get(Department.valueOf(department)) + newEmployee.getSalary().toUSD());

            return SimpleResponse.builder().status(200).message("Employee has been added. ").build();

        } catch (Exception e) {
            return SimpleResponse.builder().status(500).message("Error creating Employee. Detail: " + e.getMessage()).build();
        }
    }

    public  SimpleResponse addReports(String employeeId, String reportsToEmployeeId){
        try {
            if(!this.globalEmployeeData.containsKey(reportsToEmployeeId)){
                throw new Exception(String.format("Employee with empId : %s does not exists", reportsToEmployeeId));
            }

            if(!this.globalEmployeeData.containsKey(employeeId)){
                throw new Exception(String.format("Employee with empId : %s does not exists", employeeId));
            }

            Employee employee = this.globalEmployeeData.get(employeeId);
            Employee reportsToEmployee = this.globalEmployeeData.get(reportsToEmployeeId);

            removePreviousReports(employee);

            reportsToEmployee.getDirectReports().add(employee.getEmpId());
            employee.setReportsTo(reportsToEmployeeId);

            return SimpleResponse.builder().status(200).message(String.format("Employee %s has been added to direct reports of %s", employeeId, reportsToEmployeeId)).build();
        } catch (Exception e) {
            return SimpleResponse.builder().status(500).message("Error creating Employee. Detail: " + e.getMessage()).build();
        }
    }

    private void removePreviousReports(Employee employee) {
        if(!StringUtils.isEmpty(employee.getReportsTo())){
            Employee previousReportsTo = this.globalEmployeeData.get(employee.getReportsTo());
            previousReportsTo.getDirectReports().removeIf(dr -> dr.equals(employee.getEmpId()));
        }
    }

    public Employee getRoot(){
        Employee returnVal = globalEmployeeData.get(rootEmpId);
        return returnVal;
    }

    public Employee getEmployeeById(String id){
        Employee returnVal = globalEmployeeData.get(id);
        return returnVal;
    }

    /**
     * Write Functions:
     * 1. Find_max_number_of_direct_reports() — This function should return anyone in this organization with the highest number of direct reports
     */

    int maxReports;
    Employee maxReportEmployee;
    public EmployeeWithMaxReports getEmployeeWithMaximumDirectReports(){
        maxReports = -1;
        maxReportEmployee = null;
        Employee root = getRoot();
        getEmployeeWithMaximumDirectReports(root);
        EmployeeWithMaxReports employeeWithMaxReports = EmployeeWithMaxReports
                .builder()
                .employeeName(maxReportEmployee.getName())
                .directReports(new ArrayList<>())
                .build();
        for(String directReports : maxReportEmployee.getDirectReports()){
            employeeWithMaxReports.getDirectReports().add(this.globalEmployeeData.get(directReports).getName());
        }
        return employeeWithMaxReports;
    }

    private void getEmployeeWithMaximumDirectReports(Employee employee){
        if(employee.getDirectReports() == null || employee.getDirectReports().isEmpty()){
            return;
        }

        if(employee.getDirectReports().size() > maxReports){
            maxReports = employee.getDirectReports().size();
            maxReportEmployee = employee;
        }

        for(String directReport : employee.getDirectReports()){
            getEmployeeWithMaximumDirectReports(this.globalEmployeeData.get(directReport));
        }
    }

    public EmployeeWithMaxReports getEmployeeWithMaximumSkipLevelDirectReports(){
        maxReports = -1;
        maxReportEmployee = null;
        Employee root = getRoot();
        getEmployeeWithMaximumSkipLevelDirectReports(root);
        EmployeeWithMaxReports employeeWithMaxReports = EmployeeWithMaxReports
                .builder()
                .employeeName(maxReportEmployee.getName())
                .directReports(new ArrayList<>())
                .totalSkipLevelReports(maxReports)
                .build();
        for(String directReports : maxReportEmployee.getDirectReports()){
            employeeWithMaxReports.getDirectReports().add(this.globalEmployeeData.get(directReports).getName() +"(Direct Reports:" + this.globalEmployeeData.get(directReports).getDirectReports().size()+")");
        }
        return employeeWithMaxReports;
    }

    private int getEmployeeWithMaximumSkipLevelDirectReports(Employee employee){
        if(employee.getDirectReports() == null || employee.getDirectReports().isEmpty()){
            return 0;
        }

        int countOfSkipLevelReports = employee.getDirectReports().size();

        for(String directReport : employee.getDirectReports()){
            countOfSkipLevelReports += this.globalEmployeeData.get(directReport).getDirectReports().size();
            getEmployeeWithMaximumSkipLevelDirectReports(this.globalEmployeeData.get(directReport));
        }

        if(countOfSkipLevelReports > maxReports){
            maxReports = countOfSkipLevelReports;
            maxReportEmployee = employee;
        }
        System.out.println(employee.getName()+", directReports:"+employee.getDirectReports().size()+", directSkipLevelReports:"+countOfSkipLevelReports);
        return countOfSkipLevelReports;
    }

    /**
     * Write Functions:
     * 2. Find_common_manager(employee1, employee2) — This function should return the lowest level of common manager of any given 2 employees.
     * For example, if two employees are under 2 completely different divisions that roll up to 2 different VPs, then the common manager for those 2 employees is the CTO.
     */

    public SimpleResponse findCommonManager(String emp1, String emp2){
        try {
            if(!this.globalEmployeeData.containsKey(emp1)){
                throw new Exception(String.format("Employee with empId : %s does not exists", emp1));
            }

            if(!this.globalEmployeeData.containsKey(emp2)){
                throw new Exception(String.format("Employee with empId : %s does not exists", emp2));
            }

            List<String> employee1Hierarchy = getHierarchy(emp1);

            List<String> employee2Hierarchy = getHierarchy(emp2);

            int sameIdxOfHierarchies = employee1Hierarchy.size();
            for(int i=employee1Hierarchy.size()-1, j=employee2Hierarchy.size()-1; i>=0 && j>=0; i--, j--){
                if(!employee1Hierarchy.get(i).equals(employee2Hierarchy.get(j))){
                    break;
                }
                sameIdxOfHierarchies = i;
            }

            Employee commonManager = this.globalEmployeeData.get(employee1Hierarchy.get(sameIdxOfHierarchies));
            CommonManagerResponse commonManagerResponse = CommonManagerResponse.builder()
                    .firstEmployee(this.globalEmployeeData.get(emp1).getName())
                    .secondEmployee(this.globalEmployeeData.get(emp2).getName())
                    .commonManager(commonManager.getName())
                    .firstEmployeeCompanyHierarchy(employee1Hierarchy)
                    .secondEmployeeCompanyHierarchy(employee2Hierarchy)
                    .build();
            return SimpleResponse.builder().status(200).message(String.format("Employee %s is the common manager of %s and %s", commonManager.getEmpId(), emp1, emp2))
                    .data(commonManagerResponse)
                    .build();
        } catch (Exception e) {
            return SimpleResponse.builder().status(500).message("Error creating Employee. Detail: " + e.getMessage()).build();
        }
    }

    private List<String> getHierarchy(String empId){
        List<String> hierarchy = new ArrayList<>();
        hierarchy.add(empId);
        Employee employee = this.globalEmployeeData.get(empId);
        if(employee.getReportsTo() != null && !employee.getReportsTo().equals(empId)){
            hierarchy.addAll(getHierarchy(employee.getReportsTo()));
        }
        return hierarchy;
    }

    public SimpleResponse getBudget(String... departments) {
        AllDepartmentBudget allDepartmentBudget = new AllDepartmentBudget();
        allDepartmentBudget.setAllDepartmentBudget(new ArrayList<>());
        for(Department deptKey : this.departmentBudgetMap.keySet()){
            if(departments == null || departments.length == 0 || Arrays.stream(departments).anyMatch(d -> d.equals(deptKey.toString()))) {
                allDepartmentBudget.getAllDepartmentBudget().add(AllDepartmentBudget.DepartmentBudget.builder().department(deptKey).budget(this.departmentBudgetMap.get(deptKey)).build());
            }
        }

        return SimpleResponse.builder()
                .data(allDepartmentBudget)
                .status(200)
                .build();
    }

    public SimpleResponse updateSalary(String empId, Salary salary){
        try{
            if (!this.globalEmployeeData.containsKey(empId)) {
                throw new Exception(String.format("Employee with empId %s does not exists.", empId));
            }

            Employee employee = this.globalEmployeeData.get(empId);
            Double previousSalaryInUsd = employee.getSalary().toUSD();
            Double currentSalaryInUsd = salary.toUSD();
            Department department = employee.getDepartment();
            Double departmentsPreviousSalary = this.departmentBudgetMap.get(department);
            this.departmentBudgetMap.put(department, departmentsPreviousSalary + (currentSalaryInUsd - previousSalaryInUsd));
            employee.setSalary(salary);

            return SimpleResponse.builder()
                    .status(200)
                    .message(String.format("Department %s's budget has changed from %s to %s", department.toString(), departmentsPreviousSalary, this.departmentBudgetMap.get(department)).concat(String.format(". Employee's salary has changed from %s to %s in USD", previousSalaryInUsd, currentSalaryInUsd)))
                    .build();
        }catch (Exception e){
            return SimpleResponse.builder().status(500).message(e.getMessage()).build();
        }
    }
}
