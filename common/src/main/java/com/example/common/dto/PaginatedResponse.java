package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {
    private Collection<T> items;
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
