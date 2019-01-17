package cn.csu.sise.computerscience.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LogInFragment extends Fragment {
    private  EditText mUserTel;
    private  EditText mUserChecknum;
    private  Button mchecknumBtn;
    private  Button mloginBtn;
    private  Button mregisterBtn;
    private  SharedPreferences msharedPreferences;
    private  JSONObject responseJson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_in, parent, false);
        mUserTel = v.findViewById(R.id.usertel);
        mUserChecknum = v.findViewById(R.id.userchecknum);

        // todo 测试用，记得取消自动填写用户名和密码
        mUserTel.setText("test");
        mUserChecknum.setText("8088");

        getActivity().requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);

        mloginBtn = v.findViewById(R.id.loginbtn);
        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msharedPreferences.edit().putString("userTel", mUserTel.getText().toString()).apply();
                new accountFetchTask().execute();
            }
        });


        mregisterBtn = v.findViewById(R.id.registerbtn);
        mregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RegisterActivitiy.class);
                startActivity(i);
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(getContext().getString(R.string.login_title));

        return v;
    }

    private class accountFetchTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                JSONObject jsonObject = new JSONObject()
                        .put("userTel", mUserTel.getText())
                        .put("userValidation", mUserChecknum.getText());
                responseJson = new NetConnetcion(getContext()).Post(UrlBase.BASE + "login.json", jsonObject.toString());

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return responseJson;
        }

        @Override
        protected void onPostExecute(JSONObject responseJson) {
            super.onPostExecute(responseJson);
            try {
                if (responseJson.getString("status").equals("ok")) {
                    Intent i = new Intent(getContext(), DoctorListAcitivity.class);
                    i.putExtra(Intent.EXTRA_TEXT, msharedPreferences.getString("username", "null"));
                    Toast.makeText(getContext(), responseJson.getString("message"), Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), responseJson.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
