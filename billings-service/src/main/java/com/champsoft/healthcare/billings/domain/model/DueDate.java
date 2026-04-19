package com.champsoft.healthcare.billings.domain.model;

import java.time.LocalDate;

public class DueDate {

    private final LocalDate dueDate;

    public DueDate(LocalDate dueDate) {
        if(dueDate==null) throw new IllegalArgumentException("Due date cannot be null");

        if(dueDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Due date cannot be in the past");
        this.dueDate = dueDate;
    }

    public LocalDate dueDate() {
        return dueDate;
    }

}
