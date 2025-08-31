package com.order.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    @NotBlank
    @Size(min = 2, max = 20, message = "Full name must be between 2 and 20 characters")
    private String fullName;

    @NotNull
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @Column(unique=true)
    @Email(message = "Please Enter valid Email")
    private String emailId;

    @NotNull
    private Integer flatNumber;

    @NotNull
    private String city;

    @NotNull
    @Min(value = 100000, message = "Pincode must be a 6-digit number")
    @Max(value = 999999, message = "Pincode must be a 6-digit number")
    private Integer pincode;

    @NotBlank
    private String state;


    public String getFullAddress() {
        return flatNumber + ", " + city + ", " + state + " - " + pincode;
    }
}


