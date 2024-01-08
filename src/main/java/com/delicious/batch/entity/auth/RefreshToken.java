package com.delicious.batch.entity.auth;

import com.delicious.batch.entity.member.Member;
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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(unique = true, columnDefinition = "varchar(300)")
    private String refreshToken;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @NotNull
    @OneToOne(mappedBy = "refreshToken")
    private DeviceToken deviceToken;

    @Builder
    protected RefreshToken(String refreshToken, Member member, DeviceToken deviceToken) {
        this.refreshToken = refreshToken;

        this.member = member;
        member.addDeviceToken(deviceToken);

        this.deviceToken = deviceToken;
        deviceToken.setRefreshToken(this);
    }

    //== 비즈니스 로직 ==//
    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

    public void deleteRefreshToken() {
        member.removeRefreshToken(this);
    }

    //== Dto ==//w

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", refreshToken='" + refreshToken + '\'' +
                ", member=" + member +
                '}';
    }
}
