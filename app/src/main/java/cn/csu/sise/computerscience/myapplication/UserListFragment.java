package cn.csu.sise.computerscience.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cn.csu.sise.computerscience.myapplication.alipay.PayDemoActivity;
import cn.csu.sise.computerscience.myapplication.alipay.PayResult;

import static cn.csu.sise.computerscience.myapplication.ReservationRecordActivity.EXTRA_USER_ID;

public class UserListFragment extends Fragment {
    public static final String TAG = "UserListFragment";

    private String mUserId;
    private TextView mLogRegister;
    private TextView mReservate;
    private JSONObject mPingResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getActivity().getIntent().getStringExtra(EXTRA_USER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("我");

        mLogRegister = v.findViewById(R.id.logRegister);
        mLogRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), LogInActivity.class);
                startActivity(i);
            }
        });
        mReservate = v.findViewById(R.id.Reservate);
        mReservate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new pingTask().execute();
            }
        });

        return v;
    }

    private class pingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                mPingResponse = new NetConnetcion(getContext()).Post(UrlBase.BASE + "ping", "");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("HandlerLeak")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (mPingResponse.getString("status").equals("ok")) {
                    Intent i = new Intent(getContext(), ReservationRecordActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
