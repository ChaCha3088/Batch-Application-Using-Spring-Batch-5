package com.delicious.batch.dto;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
public class SchoolDtoWithCollegeDto {
    private final Long id;
    private final String schoolName;
    private final String branchName;
    private final List<CollegeDto> colleges;

    @Builder
    protected SchoolDtoWithCollegeDto(Long id, String schoolName, String branchName, List<CollegeDto> colleges) {
        this.id = id;
        this.schoolName = schoolName;
        this.branchName = branchName;
        this.colleges = colleges;
    }
}
