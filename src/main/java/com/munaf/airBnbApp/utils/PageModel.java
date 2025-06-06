package com.munaf.airBnbApp.utils;

import lombok.Data;

@Data
public class PageModel<T> {

    private T data;
    private Integer currentPageNumber;
    private Integer currentPageSize;
    private Integer totalPageNumber;
    private Long totalRecords;

}
