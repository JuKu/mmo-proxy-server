package com.jukusoft.mmo.proxy.core.region;

public class RegionMetaData {

    public final int regionID;
    public final int instanceID;
    public final String title;

    public RegionMetaData(int regionID, int instanceID, String regionTitle) {
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.title = regionTitle;
    }

}
