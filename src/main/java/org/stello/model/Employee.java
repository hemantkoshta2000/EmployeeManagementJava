package org.stello.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {

    /**  Requirement =>
     * Data Structures:
     * Create a data structure that supports a flexible organizational structure.  Examples: Employee -> Manager -> Director -> VP -> CTO -> CEO
     * Employee -> Manager -> Sr. Manager -> Director -> Sr. Director -> VP -> Sr. VP -> CTO -> CEO
     * There could be any number of layers and roles.
     *
     * Approach & Considerations =>
     * Tree Data structure will be best to store the relationship between employees and their reporters
     * For taking advantage as graph and two-way traversals I have taken 'reportTo' and 'directReport(s)' into consideration in this Model.
     * HashMap should be used to faster access Employee Data. We will use a String(employeeId) to EmployeeTreeNode(employee's details) Map.
     * This class will hold all the data of all the employees                                                                                   *
     * Implementation and Usage will be in the repository class.
     * */

    String name;
    String empId;
    Role role;
    Department department;
    String reportsTo;
    List<String> directReports;
    Salary salary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee employee)) return false;
        return Objects.equals(empId, employee.empId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", empId='" + empId + '\'' +
                ", role=" + role +
                ", department=" + department +
                '}';
    }
}
