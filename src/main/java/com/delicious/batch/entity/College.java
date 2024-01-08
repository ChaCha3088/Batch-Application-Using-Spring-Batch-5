package com.delicious.batch.entity;

import static lombok.AccessLevel.PROTECTED;

import com.delicious.batch.dto.CollegeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class College extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COLLEGE_ID")
    private Long id;

    @NotBlank
    private String collegeName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHOOL_ID")
    private School school;

    @NotNull
    @OneToMany(mappedBy = "college")
    private Set<Board> boards = new HashSet<>();

    @Builder
    protected College(String collegeName, School school) {
        this.collegeName = collegeName;
        this.school = school;

        school.addCollege(this);
    }

    //-- 연관 관계 편의 메소드 --//
    public void addBoard(Board board) {
        this.boards.add(board);
    }

    //-- DTO 생성 메소드 --//
    public CollegeDto toDto() {
        return CollegeDto.builder()
            .id(this.id)
            .collegeName(this.collegeName)
            .schoolId(this.school.getId())
            .build();
    }
}
