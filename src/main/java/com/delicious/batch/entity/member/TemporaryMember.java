package com.delicious.batch.entity.member;

import com.delicious.batch.dto.TemporaryMemberDto;
import com.delicious.batch.entity.Address;
import com.delicious.batch.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemporaryMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Address address;

    @NotBlank
    private String verificationCode;

    @Builder
    protected TemporaryMember(String name, String email, String password, String city, String street, String zipcode, String verificationCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = Address.builder()
                        .city(city)
                        .street(street)
                        .zipcode(zipcode)
                .build();
        this.verificationCode = verificationCode;
    }

    //== 비즈니스 로직 ==//
    public void updateVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    //== Dto ==//
    public TemporaryMemberDto toDto() {
        return TemporaryMemberDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .city(address.getCity())
                .street(address.getStreet())
                .zipcode(address.getZipcode())
                .verificationCode(verificationCode)
                .build();
    }
}
