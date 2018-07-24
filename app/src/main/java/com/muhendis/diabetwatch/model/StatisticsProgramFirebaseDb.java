package com.muhendis.diabetwatch.model;

public class StatisticsProgramFirebaseDb {
    String pid,uid,finishDate,diastole,systole,pulse,diabetes,uid_finishDate;
    boolean isBeforeStart;

    public StatisticsProgramFirebaseDb() {
    }

    public StatisticsProgramFirebaseDb(String pid, String uid, String finishDate, String diastole, String systole, String pulse, String diabetes, boolean isBeforeStart) {
        this.pid = pid;
        this.uid = uid;
        this.finishDate = finishDate;
        this.diastole = diastole;
        this.systole = systole;
        this.pulse = pulse;
        this.diabetes = diabetes;
        this.isBeforeStart = isBeforeStart;
        this.uid_finishDate=uid+"_"+finishDate;
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

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getDiastole() {
        return diastole;
    }

    public void setDiastole(String diastole) {
        this.diastole = diastole;
    }

    public String getSystole() {
        return systole;
    }

    public void setSystole(String systole) {
        this.systole = systole;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(String diabetes) {
        this.diabetes = diabetes;
    }

    public String getUid_finishDate() {
        return uid_finishDate;
    }

    public void setUid_finishDate(String uid_finishDate) {
        this.uid_finishDate = uid_finishDate;
    }

    public boolean getIsBeforeStart() {
        return isBeforeStart;
    }

    public void setIsBeforeStart(boolean isBeforeStart) {
        isBeforeStart = isBeforeStart;
    }
}
