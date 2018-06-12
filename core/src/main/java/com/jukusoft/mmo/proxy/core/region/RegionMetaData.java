package com.jukusoft.mmo.proxy.core.region;

public class RegionMetaData {

    public final int regionID;
    public final int instanceID;
    public final String title;

    public final int xPos;
    public final int yPos;
    public final int zPos;

    public RegionMetaData(int regionID, int instanceID, String regionTitle, int xPos, int yPos, int zPos) {
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.title = regionTitle;

        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

}
