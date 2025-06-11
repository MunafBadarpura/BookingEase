package com.munaf.airBnbApp.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageModel {
    private Object content;
    private Integer currentPageNumber;
    private Integer currentPageSize;
    private Integer totalPageNumber;
    private Long totalRecords;
}
