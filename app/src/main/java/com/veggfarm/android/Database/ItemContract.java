package com.veggfarm.android.Database;

import android.provider.BaseColumns;

/**
 * created by Ashish Rawat
 */

public class ItemContract {
    public static abstract class ItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "item";
        public static final String COST_PER_KG = "cost_per_kg";
        public static final String ITEM_IMAGE = "item_image";
        public static final String ITEM_NAME = "item_name";
        public static final String TOTAL_NUMBER = "total_number";
        public static final String ITEM_ID ="item_id" ;
    }
}
