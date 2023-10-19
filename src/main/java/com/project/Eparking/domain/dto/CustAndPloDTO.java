package com.project.Eparking.domain.dto;

import java.util.List;

public class CustAndPloDTO {
    private long totalCustomer;
    private long totalPlo;

    public long getTotalCustomer() {
        return totalCustomer;
    }

    public void setTotalCustomer(long totalCustomer) {
        this.totalCustomer = totalCustomer;
    }

    public long getTotalPlo() {
        return totalPlo;
    }

    public void setTotalPlo(long totalPlo) {
        this.totalPlo = totalPlo;
    }

    public CustAndPloDTO(long totalCustomer, long totalPlo) {
        this.totalCustomer = totalCustomer;
        this.totalPlo = totalPlo;
    }
}

