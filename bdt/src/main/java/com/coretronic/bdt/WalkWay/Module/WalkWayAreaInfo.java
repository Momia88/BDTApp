package com.coretronic.bdt.WalkWay.Module;

/**
 * Created by Morris on 14/12/1.
 */
public class WalkWayAreaInfo {

    private String partArea;
    private int walkways;
    private int visiteds;
    private int mapIconRid;
    private int percentRid;

    public void setPartArea(String partArea) {
        this.partArea = partArea;
    }

    public void setWalkways(int walkways) {
        this.walkways = walkways;
    }

    public void setVisiteds(int visiteds) {
        this.visiteds = visiteds;
    }

    public void setMapIconRid(int mapIconRid) {
        this.mapIconRid = mapIconRid;
    }

    public void setPercentRid(int percentRid) {
        this.percentRid = percentRid;
    }

    public String getPartArea() {
        return partArea;
    }

    public int getWalkways() {
        return walkways;
    }

    public int getVisiteds() {
        return visiteds;
    }

    public int getMapIconRid() {
        return mapIconRid;
    }

    public int getPercentRid() {
        return percentRid;
    }
}
