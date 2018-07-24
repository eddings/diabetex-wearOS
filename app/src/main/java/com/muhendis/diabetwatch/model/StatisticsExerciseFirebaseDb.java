package com.muhendis.diabetwatch.model;

import java.util.List;

public class StatisticsExerciseFirebaseDb {

    String finishDate,eid,pid,uid,uid_finishDate,uid_pid_finishDate;
    int elapsedTime,stepCounter,walkedDistance;
    List<String> heartRate;
    List<String> walkingSpeed;
    boolean isWalking;

    public StatisticsExerciseFirebaseDb() {
    }

    public StatisticsExerciseFirebaseDb(String finishDate, String eid, String pid, String uid, int elapsedTime, int stepCounter,List<String> heartRate) {
        this.finishDate = finishDate;
        this.eid = eid;
        this.pid = pid;
        this.uid = uid;
        this.elapsedTime = elapsedTime;
        this.stepCounter = stepCounter;
        this.heartRate = heartRate;
        this.uid_finishDate = uid+"_"+finishDate;
        this.uid_pid_finishDate = uid+"_"+pid+"_"+finishDate;
        this.isWalking = false;
        this.walkedDistance = 0;
    }
    public StatisticsExerciseFirebaseDb(String finishDate, String eid, String pid, String uid, int elapsedTime, int stepCounter,List<String> heartRate,List<String> walkingSpeed,boolean isWalking, int walkedDistance) {
        this.finishDate = finishDate;
        this.eid = eid;
        this.pid = pid;
        this.uid = uid;
        this.elapsedTime = elapsedTime;
        this.stepCounter = stepCounter;
        this.heartRate = heartRate;
        this.uid_finishDate = uid+"_"+finishDate;
        this.uid_pid_finishDate = uid+"_"+pid+"_"+finishDate;
        this.walkingSpeed = walkingSpeed;
        this.isWalking = isWalking;
        this.walkedDistance = walkedDistance;
    }

    public int getWalkedDistance() {
        return walkedDistance;
    }

    public void setWalkedDistance(int walkedDistance) {
        this.walkedDistance = walkedDistance;
    }

    public List<String> getWalkingSpeed() {
        return walkingSpeed;
    }

    public void setWalkingSpeed(List<String> walkingSpeed) {
        this.walkingSpeed = walkingSpeed;
    }

    public boolean getIsWalking() {
        return isWalking;
    }

    public void setIsWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }


    public List<String> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(List<String> heartRate) {
        this.heartRate = heartRate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid_finishDate() {
        return uid_finishDate;
    }

    public void setUid_finishDate(String uid_finishDate) {
        this.uid_finishDate = uid_finishDate;
    }

    public String getUid_pid_finishDate() {
        return uid_pid_finishDate;
    }

    public void setUid_pid_finishDate(String uid_pid_finishDate) {
        this.uid_pid_finishDate = uid_pid_finishDate;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getStepCounter() {
        return stepCounter;
    }

    public void setStepCounter(int stepCounter) {
        this.stepCounter = stepCounter;
    }
}
