package cn.csu.sise.computerscience.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class DoctorSelfInfoActivity extends MainFragmentActivity {
    public static final String EXTRA_DOCTOR_ID = "cn.csu.sise.computerscience.myapplication.doctor_id";

    public static Intent getIntent(Context context, String doctorId){
        Intent i = new Intent(context, DoctorSelfInfoActivity.class);
        i.putExtra(EXTRA_DOCTOR_ID, doctorId);
        return i;
    }
    protected Fragment createFragment(){
        return new DoctorSelfInfoFragment();
    }

}
