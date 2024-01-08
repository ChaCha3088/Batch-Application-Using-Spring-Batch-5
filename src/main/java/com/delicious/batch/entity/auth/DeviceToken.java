package com.delicious.batch.entity.auth;

import static jakarta.persistence.FetchType.LAZY;

import com.delicious.batch.dto.DeviceTokenDto;
import com.delicious.batch.entity.BaseEntity;
import com.delicious.batch.entity.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_TOKEN_ID")
    private long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @NotBlank
    @Column(unique = true)
    private String deviceToken;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "REFRESH_TOKEN_ID")
    private RefreshToken refreshToken;

    @Builder
    protected DeviceToken(Member member, String deviceToken) {
        this.member = member;
        this.deviceToken = deviceToken;
        member.getDeviceTokens().add(this);
    }

    // 연관관계 편의 메서드
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void removeRefreshToken() {
        this.refreshToken = null;
    }

    public void deleteDeviceToken() {
        this.member.getDeviceTokens().remove(this);
    }

    // DTO
    public DeviceTokenDto toDeviceTokenDto() {
        return DeviceTokenDto.builder()
            .id(this.id)
            .memberId(this.member.getId())
            .deviceToken(this.deviceToken)
            .refreshTokenId(this.refreshToken.getId())
            .build();
    }
}
