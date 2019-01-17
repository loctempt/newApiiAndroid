package cn.csu.sise.computerscience.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import static cn.csu.sise.computerscience.myapplication.ReservationRecordActivity.EXTRA_USER_ID;

public class UserListFragment extends Fragment {
    public static final String TAG = "UserListFragment";

    private String mUserId;
   private TextView mLogRegister;
   private TextView mReservate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getActivity().getIntent().getStringExtra(EXTRA_USER_ID);
        //  todo  底部导航点击切换页面

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user_list, container, false);
        mLogRegister=v.findViewById(R.id.logRegister);
        mLogRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(),LogInActivity.class);
                startActivity(i);
            }
        });
        mReservate=v.findViewById(R.id.Reservate);
        mReservate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(),ReservationRecordActivity.class);
                startActivity(i);
            }
        });

        return v;
    }


}
