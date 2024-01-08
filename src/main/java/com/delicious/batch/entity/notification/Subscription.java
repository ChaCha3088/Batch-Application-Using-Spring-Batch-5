package com.delicious.batch.entity.notification;

import static jakarta.persistence.FetchType.LAZY;

import com.delicious.batch.entity.BaseEntity;
import com.delicious.batch.entity.Board;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBSCRIPTION_ID")
    private long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Builder
    protected Subscription(Member member, Board board) {
        this.member = member;
        this.board = board;

        member.getSubscriptions().add(this);
        board.getSubscriptions().add(this);
    }

    // 연관관계 편의 메소드
    public void removeSubscription() {
        this.member.getSubscriptions().remove(this);
        this.board.getSubscriptions().remove(this);
    }
}
