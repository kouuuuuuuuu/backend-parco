package com.project.Eparking.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePloStatusDTO {
    private String ploId;
    private int newStatus;

    public UpdatePloStatusDTO() {
    }
}
