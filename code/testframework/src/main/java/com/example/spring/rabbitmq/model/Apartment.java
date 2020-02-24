package com.example.spring.rabbitmq.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Apartment {

    private Long id;

    private String name;

    private Long hostId;

    private String hostName;

    private String neighbourhoodGroup;

    private String neighbourhood;

    private String latitude;

    private String longitude;

    private String roomType;

    private Long price;

    private Long minimumNights;

    private Long numberOfReviews;

    private String lastReview;

    private String reviewPerMonth;

    private String calculatedHostListingsCount;

    private Long availability365;
}
