package com.delicious.batch.dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
public class CollegeDto {
    private final Long id;
    private final String collegeName;
    private final Long schoolId;

    @Builder
    protected CollegeDto(Long id, String collegeName, Long schoolId) {
        this.id = id;
        this.collegeName = collegeName;
        this.schoolId = schoolId;
    }
}
