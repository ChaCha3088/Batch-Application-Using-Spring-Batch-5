package com.delicious.batch.entity;

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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long id;

    @NotBlank
    private String boardName;

    @NotBlank
    private String boardCrawlingUrl;

    @NotBlank
    private String boardViewUrl;

    @NotNull
    private Boolean isThereNotice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLEGE_ID")
    private College college;

    @NotNull
    @OneToMany(mappedBy = "board")
    private List<Notification> notifications = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "board")
    private List<Subscription> subscriptions = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "board")
    private List<Article> articles = new ArrayList();

    @Builder
    protected Board(String boardName, String boardCrawlingUrl, String boardViewUrl, Boolean isThereNotice, College college) {
        this.boardName = boardName;
        this.boardCrawlingUrl = boardCrawlingUrl;
        this.boardViewUrl = boardViewUrl;
        this.isThereNotice = isThereNotice;
        this.college = college;

        college.addBoard(this);
    }
}
