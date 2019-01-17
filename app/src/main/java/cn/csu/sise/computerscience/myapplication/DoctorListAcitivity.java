package cn.csu.sise.computerscience.myapplication;

import android.support.v4.app.Fragment;

public class DoctorListAcitivity extends MainFragmentActivity {
    protected Fragment createFragment(){
        return new DoctorListFragment();
    }

}
