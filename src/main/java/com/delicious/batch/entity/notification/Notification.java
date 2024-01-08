package com.delicious.batch.entity.notification;

import static lombok.AccessLevel.PROTECTED;

import com.delicious.batch.entity.Article;
import com.delicious.batch.entity.BaseEntity;
import com.delicious.batch.entity.Board;
import com.delicious.batch.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Member.class)
    @JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false)
    private Member member;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    @Column(name = "BOARD_ID", nullable = false)
    private Long boardId;
    @JoinColumn(name = "BOARD_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Board.class)
    private Board board;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    private Article article;

    @NotNull
    private Boolean isRead;

    @Builder
    protected Notification(Long memberId, Board board, Article article) {
        this.memberId = memberId;
        this.boardId = board.getId();
        this.article = article;

        this.article.addNotification(this);

        this.isRead = false;
    }

    //== Dto ==//
    public NotificationDtoWithBoardViewUrlAndArticleDto toDtoWithBoardViewUrlAndArticleDto() {
        return NotificationDtoWithBoardViewUrlAndArticleDto.builder()
            .id(this.id)
            .memberId(this.memberId)
            .boardName(this.board.getBoardName())
            .boardViewUrl(this.board.getBoardViewUrl())
            .articleDto(this.article.toDto())
            .isRead(this.isRead)
            .build();
    }
}
