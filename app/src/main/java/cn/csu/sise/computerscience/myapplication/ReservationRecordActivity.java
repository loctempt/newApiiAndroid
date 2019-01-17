package cn.csu.sise.computerscience.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ReservationRecordActivity extends MainFragmentActivity{
    public static final String EXTRA_USER_ID = "cn.csu.sise.computerscience.myapplication.user_id";

    protected Fragment createFragment(){
        return new ReservationRecordFragment();
    }
    public static Intent getIntent(Context context, String userId) {
        Intent i = new Intent(context, ReservationRecordActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        return i;
    }

}
