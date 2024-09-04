package com.woosuk.AgingInPlace.restClient.models;

import java.time.Instant;

public class SleepDataVO {
    private int member_id;
    private String name;
    private Instant birthDate;
    private int awake;
    private Instant bedTimeEnd;
    private Instant bedTimeStart;
    private double breathAverage;
    private int deep;
    private int duration;
    private int efficiency;
    private double HRAverage;
    private double HRLowest;
    private boolean longest;
    private int light;
    private int midpointTime;
    private int rem;
    private int restless;
    private int score;
    private int scoreAlignment;
    private int scoreDeep;
    private int scoreDisturbances;
    private int scoreEfficiency;
    private int scoreRem;
    private int scoreTotal;
    private int total;
    private boolean patient;

    // Constructor
    public SleepDataVO(int member_id, String name, Instant birthDate, int awake,
                       Instant bedTimeEnd, Instant bedTimeStart, double breathAverage, int deep,
                       int duration, int efficiency, double HRAverage, double HRLowest,
                       boolean longest, int light, int midpointTime, int rem, int restless,
                       int score, int scoreAlignment, int scoreDeep, int scoreDisturbances,
                       int scoreEfficiency, int scoreRem, int scoreTotal, int total, boolean patient) {
        this.member_id = member_id;
        this.name = name;
        this.birthDate = birthDate;
        this.awake = awake;
        this.bedTimeEnd = bedTimeEnd;
        this.bedTimeStart = bedTimeStart;
        this.breathAverage = breathAverage;
        this.deep = deep;
        this.duration = duration;
        this.efficiency = efficiency;
        this.HRAverage = HRAverage;
        this.HRLowest = HRLowest;
        this.longest = longest;
        this.light = light;
        this.midpointTime = midpointTime;
        this.rem = rem;
        this.restless = restless;
        this.score = score;
        this.scoreAlignment = scoreAlignment;
        this.scoreDeep = scoreDeep;
        this.scoreDisturbances = scoreDisturbances;
        this.scoreEfficiency = scoreEfficiency;
        this.scoreRem = scoreRem;
        this.scoreTotal = scoreTotal;
        this.total = total;
        this.patient = patient;
    }
}