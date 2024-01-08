package com.delicious.batch.entity.member;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.delicious.batch.entity.Address;
import com.delicious.batch.entity.BaseEntity;
import com.delicious.batch.entity.auth.DeviceToken;
import com.delicious.batch.entity.auth.RefreshToken;
import com.delicious.batch.entity.notification.Notification;
import com.delicious.batch.entity.notification.Subscription;
import com.delicious.batch.enumstorage.role.MemberRole;
import com.delicious.batch.enumstorage.status.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    private String email;

    private String password;

    @Embedded
    @NotNull
    private Address address;

    @NotNull
    @OneToMany(mappedBy = "member")
    private Set<Subscription> subscriptions = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "member")
    private Set<DeviceToken> deviceTokens = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "member")
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "member")
    private Set<Notification> notifications = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @NotNull
    private int logInAttempt = 0;

    @NotNull
    private Boolean consentToReceivePushNotification;

    private String passwordVerificationCode;
    private LocalDateTime passwordVerificationCodeExpiration;

    @Builder
    protected Member(String name, String email, String password, String city, String street, String zipcode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = Address.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
            .build();
        this.role = MemberRole.MEMBER;
        this.status = MemberStatus.ACTIVE;
        this.consentToReceivePushNotification = false;
    }

    //== 비즈니스 로직 ==//
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateMemberAddress(String city, String street, String zipcode) {
        this.address = Address.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .build();
    }

    public void unlock() {
        if (this.status == MemberStatus.LOCKED) {
            this.status = MemberStatus.ACTIVE;
            this.logInAttempt = 0;
        }
    }

    public MemberStatus countUpLogInAttempt() {
        this.logInAttempt += 1;

        if (this.logInAttempt >= 5) {
            if (this.status != MemberStatus.DELETED) {
                return this.status = MemberStatus.LOCKED;
            }
        }

        return this.status;
    }

    public void resetLogInAttempt() {
        this.logInAttempt = 0;
    }

    public void updatePasswordVerificationCode(String passwordVerificationCode) {
        this.passwordVerificationCode = passwordVerificationCode;
        this.passwordVerificationCodeExpiration = LocalDateTime.now().plusMinutes(10);
    }

    public void deletePasswordVerificationCode() {
        this.passwordVerificationCode = null;
        this.passwordVerificationCodeExpiration = null;
    }

    public void deleteMember() {
        this.status = MemberStatus.DELETED;
    }

    public void consentToReceivePushNotification() {
        this.consentToReceivePushNotification = true;
    }

    public void refuseToReceivePushNotification() {
        this.consentToReceivePushNotification = false;
    }

    //==연관관계 메소드==//
    public void setStore(Store store) {
        stores.add(store);
    }

    public void addOAuth2(OAuth2 oAuth2) {
        oAuth2s.add(oAuth2);
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshTokens.add(refreshToken);
    }

    public boolean removeRefreshToken(RefreshToken refreshToken) {
        return this.refreshTokens.remove(refreshToken);
    }

    public void addDeviceToken(DeviceToken deviceToken) {
        deviceTokens.add(deviceToken);
    }

    //==DTO==//
    public MemberFindDto toMemberFindDto() {
        return MemberFindDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(AddressDto.builder()
                        .city(address.getCity())
                        .street(address.getStreet())
                        .zipcode(address.getZipcode())
                        .build())
                .consentToReceivePushNotification(consentToReceivePushNotification)
            .build();
    }
}
