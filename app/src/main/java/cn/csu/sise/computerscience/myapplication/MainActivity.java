package cn.csu.sise.computerscience.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main_page_container);
        fm = getSupportFragmentManager();
        if (fragment == null) {
            fragment = new DoctorListFragment();
            fm.beginTransaction().add(R.id.main_page_container, fragment).commit();
        }

        BottomNavigationView mNavigationView = findViewById(R.id.bnv_menu);

        mNavigationView = findViewById(R.id.bnv_menu);
        final FragmentManager finalFm = fm;
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_main:
                        finalFm.beginTransaction()
                                .replace(R.id.main_page_container, new DoctorListFragment())
                                .commit();
                        break;
                    case R.id.menu_item_me:
                        finalFm.beginTransaction()
                                .replace(R.id.main_page_container, new UserListFragment())
                                .commit();
                        break;
                }
                return false;
            }
        });
    }
}
