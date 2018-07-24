package com.muhendis.diabetwatch.model;

public class ProgramExerciseFirebaseDb {
    int dailyRep,hold,reps,sets,weeklyRep,minWalkingSpeed,maxWalkingSpeed,duration;
    String eid,pid,uid,instruction,name,uid_pid,videoLink,photoLink;
    boolean isWalking;

    public ProgramExerciseFirebaseDb() {
    }

    public ProgramExerciseFirebaseDb(int dailyRep, String eid, int hold, String pid, int reps, int sets, String uid, int weeklyRep, String instruction, String name, String videoLink, String photoLink) {
        this.dailyRep = dailyRep;
        this.eid = eid;
        this.hold = hold;
        this.pid = pid;
        this.reps = reps;
        this.sets = sets;
        this.uid = uid;
        this.weeklyRep = weeklyRep;
        this.instruction = instruction;
        this.name = name;
        this.videoLink = videoLink;
        this.photoLink = photoLink;
        this.uid_pid = String.valueOf(uid)+"_"+ String.valueOf(pid);
        this.isWalking = false;
        this.minWalkingSpeed = 0;
        this.maxWalkingSpeed = 0;
        this.duration = 0;
    }

    public ProgramExerciseFirebaseDb(int dailyRep, String eid, int hold, String pid, int reps, int sets, String uid, int weeklyRep, String instruction, String name, String videoLink, String photoLink,boolean isWalking,int minWalkingSpeed,int maxWalkingSpeed,int duration) {
        this.dailyRep = dailyRep;
        this.hold = hold;
        this.reps = reps;
        this.sets = sets;
        this.weeklyRep = weeklyRep;
        this.minWalkingSpeed = minWalkingSpeed;
        this.maxWalkingSpeed = maxWalkingSpeed;
        this.eid = eid;
        this.pid = pid;
        this.uid = uid;
        this.instruction = instruction;
        this.name = name;
        this.uid_pid = String.valueOf(uid)+"_"+ String.valueOf(pid);
        this.videoLink = videoLink;
        this.photoLink = photoLink;
        this.isWalking = isWalking;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMinWalkingSpeed() {
        return minWalkingSpeed;
    }

    public void setMinWalkingSpeed(int minWalkingSpeed) {
        this.minWalkingSpeed = minWalkingSpeed;
    }

    public int getMaxWalkingSpeed() {
        return maxWalkingSpeed;
    }

    public void setMaxWalkingSpeed(int maxWalkingSpeed) {
        this.maxWalkingSpeed = maxWalkingSpeed;
    }

    public boolean getIsWalking() {
        return isWalking;
    }

    public void setIsWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }


    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public int getDailyRep() {
        return dailyRep;
    }

    public void setDailyRep(int dailyRep) {
        this.dailyRep = dailyRep;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getHold() {
        return hold;
    }

    public void setHold(int hold) {
        this.hold = hold;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getWeeklyRep() {
        return weeklyRep;
    }

    public void setWeeklyRep(int weeklyRep) {
        this.weeklyRep = weeklyRep;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid_pid() {
        return uid_pid;
    }

    public void setUid_pid(String uid_pid) {
        this.uid_pid = uid_pid;
    }
}
