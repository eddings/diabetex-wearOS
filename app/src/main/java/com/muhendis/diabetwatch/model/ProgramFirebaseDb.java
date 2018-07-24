package com.muhendis.diabetwatch.model;

public class ProgramFirebaseDb {
    String did,pid,uid;
    boolean isFinished;
    String diagnosis,finishDate,name,startDate;

    public ProgramFirebaseDb() {
    }

    public ProgramFirebaseDb(String did, String pid, String uid, boolean isFinished, String diagnosis, String finishDate, String name, String startDate) {
        this.did = did;
        this.pid = pid;
        this.uid = uid;
        this.isFinished = isFinished;
        this.diagnosis = diagnosis;
        this.finishDate = finishDate;
        this.name = name;
        this.startDate = startDate;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
