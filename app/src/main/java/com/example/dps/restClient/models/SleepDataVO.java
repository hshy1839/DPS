package com.example.dps.restClient.models;

import java.time.Instant;
import java.util.Date;

public class SleepDataVO extends AvroRESTVO {
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

    public SleepDataVO(String username, String name, Instant birthDate, int awake,
                       Instant bedTimeEnd, Instant bedTimeStart, double breathAverage, int deep, int duration,
                       double HRAverage, double HRLowest, int light, int rem, int total, boolean patient) {
        super(username, name, birthDate, patient);
        this.awake = awake;
        this.bedTimeEnd = bedTimeEnd;
        this.bedTimeStart = bedTimeStart;
        this.breathAverage = breathAverage;
        this.deep = deep;
        this.duration = duration;
        this.HRAverage = HRAverage;
        this.HRLowest = HRLowest;
        this.light = light;
        this.rem = rem;
        this.total = total;
    }

    private Instant getBedTimeEnd() {
        return bedTimeEnd;
    }

    private Instant getBedTimeStart() {
        return bedTimeStart;
    }

    @Override
    protected String valueSchemaMessage() {
        String message = "\"value_schema\": \"{" +
                "\\\"name\\\": \\\"sleep_data_value\\\"," +
                "\\\"type\\\": \\\"record\\\"," +
                "\\\"fields\\\": [" +
                "{\\\"name\\\": \\\"username\\\", \\\"type\\\": "+ this.javaTypeToAvroType(this.getUsername()) +"}," +
                "{\\\"name\\\": \\\"name\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getName())+"}," +
                "{\\\"name\\\": \\\"birth_date\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getBirthDate())+"}," +
                "{\\\"name\\\": \\\"create_date\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getCreateDate())+"}," +
                "{\\\"name\\\": \\\"sleep_awake\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.awake)+"}," +
                "{\\\"name\\\": \\\"sleep_bedtime_end\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.bedTimeEnd)+"}," +
                "{\\\"name\\\": \\\"sleep_bedtime_start\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.bedTimeStart)+"}," +
                "{\\\"name\\\": \\\"sleep_breath_average\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.breathAverage)+"}," +
                "{\\\"name\\\": \\\"sleep_deep\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.deep)+"}," +
                "{\\\"name\\\": \\\"sleep_duration\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.duration)+"}," +
                "{\\\"name\\\": \\\"sleep_efficiency\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.efficiency)+"}," +
                "{\\\"name\\\": \\\"sleep_hr_average\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.HRAverage)+"}," +
                "{\\\"name\\\": \\\"sleep_hr_lowest\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.HRLowest)+"}," +
                "{\\\"name\\\": \\\"sleep_is_longest\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.longest)+"}," +
                "{\\\"name\\\": \\\"sleep_light\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.light)+"}," +
                "{\\\"name\\\": \\\"sleep_midpoint_time\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.midpointTime)+"}," +
                "{\\\"name\\\": \\\"sleep_rem\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.rem)+"}," +
                "{\\\"name\\\": \\\"sleep_restless\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.restless)+"}," +
                "{\\\"name\\\": \\\"sleep_score\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.score)+"}," +
                "{\\\"name\\\": \\\"sleep_score_alignment\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreAlignment)+"}," +
                "{\\\"name\\\": \\\"sleep_score_deep\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreDeep)+"}," +
                "{\\\"name\\\": \\\"sleep_score_disturbances\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreDisturbances)+"}," +
                "{\\\"name\\\": \\\"sleep_score_efficiency\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreEfficiency)+"}," +
                "{\\\"name\\\": \\\"sleep_score_rem\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreRem)+"}," +
                "{\\\"name\\\": \\\"sleep_score_total\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreTotal)+"}," +
                "{\\\"name\\\": \\\"sleep_total\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.total)+"}," +
                "{\\\"name\\\": \\\"is_patient\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.isPatient())+"}" +
                "]}\"";
        return message;
    }

    @Override
    protected String valueMessage() {
        String message = "\"records\":" +
                "[{\"value\": {" +
                "\"username\": \""+this.getUsername()+"\"," +
                "\"name\": \""+this.getName()+"\"," +
                "\"birth_date\": "+this.getBirthDate().getEpochSecond()+"," +
                "\"create_date\": "+this.getCreateDate().getEpochSecond()+"," +
                "\"sleep_awake\": "+this.awake+"," +
                "\"sleep_bedtime_end\": "+this.getBedTimeEnd().getEpochSecond()+"," +
                "\"sleep_bedtime_start\": "+this.getBedTimeStart().getEpochSecond()+"," +
                "\"sleep_breath_average\": "+this.breathAverage+"," +
                "\"sleep_deep\": "+this.deep+"," +
                "\"sleep_duration\": "+this.duration+"," +
                "\"sleep_efficiency\": "+this.efficiency+"," +
                "\"sleep_hr_average\": "+this.HRAverage+"," +
                "\"sleep_hr_lowest\": "+this.HRLowest+"," +
                "\"sleep_is_longest\": "+this.longest+"," +
                "\"sleep_light\": "+this.light+"," +
                "\"sleep_midpoint_time\": "+this.midpointTime+"," +
                "\"sleep_rem\": "+this.rem+"," +
                "\"sleep_restless\": "+this.restless+"," +
                "\"sleep_score\": "+this.score+"," +
                "\"sleep_score_alignment\": "+this.scoreAlignment+"," +
                "\"sleep_score_deep\": "+this.scoreDeep+"," +
                "\"sleep_score_disturbances\": "+this.scoreDisturbances+"," +
                "\"sleep_score_efficiency\": "+this.scoreEfficiency+"," +
                "\"sleep_score_rem\": "+this.scoreRem+"," +
                "\"sleep_score_total\": "+this.scoreTotal+"," +
                "\"sleep_total\": "+this.total+"," +
                "\"is_patient\": "+this.isPatient() +
                "}}]";
        return message;
    }
}
