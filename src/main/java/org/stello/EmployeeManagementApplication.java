package org.stello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.stello.repository.EmployeesDataRepository;

@SpringBootApplication
public class EmployeeManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class);
    }

    @Bean
    public EmployeesDataRepository employeesDataRepository(){
        return new EmployeesDataRepository();
    }
}