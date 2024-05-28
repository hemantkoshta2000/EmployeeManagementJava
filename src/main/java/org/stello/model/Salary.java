package org.stello.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Salary {
    Double salary;
    String currency;

    public Double toINR(){
        if(currency.equals("USD")){
            return this.salary*80.0D;
        }

        if(currency.equals("GBP")){
            return this.salary*100.0D;
        }

        return this.salary;
    }

    public Double toGBP(){
        if(currency.equals("USD")){
            return this.salary/1.25D;
        }

        if(currency.equals("INR")){
            return this.salary/100.0D;
        }

        return this.salary;
    }

    public Double toUSD(){
        if(currency.equals("INR")){
            return this.salary/80.0D;
        }

        if(currency.equals("GBP")){
            return this.salary*1.25D;
        }

        return this.salary;
    }
}
