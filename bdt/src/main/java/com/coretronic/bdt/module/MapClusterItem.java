package com.coretronic.bdt.module;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Morris on 2014/9/10.
 */
public class MapClusterItem  implements ClusterItem{

    public final String name;
    public final int index;
    private final LatLng mPosition;

    public MapClusterItem(LatLng position,String name,int index) {
        this.name = name;
        this.index = index;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}

