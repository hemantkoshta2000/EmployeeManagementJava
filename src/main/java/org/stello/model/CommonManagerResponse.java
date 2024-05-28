package org.stello.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonManagerResponse {
    String firstEmployee;
    String secondEmployee;
    String commonManager;
    List<String> firstEmployeeCompanyHierarchy;
    List<String> secondEmployeeCompanyHierarchy;
}
