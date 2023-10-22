package com.project.Eparking.domain.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMonthANDYear {
    @ApiModelProperty(example= "01-2023")
    private String MonthAndYear;
}
