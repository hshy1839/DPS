package com.woosuk.AgingInPlace.restClient.models;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public class ActivityDataVO {
    private int member_id;
    private String name;
    private Instant birthDate;
    private int calActive;
    private int calTotal;
    private int dailyMovement;
    private Instant dayEnd;
    private Instant dayStart;
    private int high;
    private int inactive;
    private int inactivityAlerts;
    private int low;
    private int medium;
    private int nonWear;
    private int score;
    private int scoreMeetDailyTargets;
    private int steps;
    private int total;
    private boolean patient;

    // 생성자
    public ActivityDataVO(int member_id, String name, Instant birthDate, int calActive,
                          int calTotal, int dailyMovement, Instant dayEnd, Instant dayStart, int high, int inactive,
                          int inactivityAlerts, int low, int medium, int nonWear, int score, int scoreMeetDailyTargets,
                          int steps, int total, boolean patient) {
        this.member_id = member_id;
        this.name = name;
        this.birthDate = birthDate;
        this.calActive = calActive;
        this.calTotal = calTotal;
        this.dailyMovement = dailyMovement;
        this.dayEnd = dayEnd;
        this.dayStart = dayStart;
        this.high = high;
        this.inactive = inactive;
        this.inactivityAlerts = inactivityAlerts;
        this.low = low;
        this.medium = medium;
        this.nonWear = nonWear;
        this.score = score;
        this.scoreMeetDailyTargets = scoreMeetDailyTargets;
        this.steps = steps;
        this.total = total;
        this.patient = patient;
    }
}