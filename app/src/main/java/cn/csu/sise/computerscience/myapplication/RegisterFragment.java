package cn.csu.sise.computerscience.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterFragment extends Fragment  implements RadioGroup.OnCheckedChangeListener {
    private EditText muserName;
    private EditText musrAge;
    private EditText muserTel;
    private EditText muserchecknum;
    private RadioGroup muserSex;
    private Button mchecknumBtn;
    private Button mregisterBtn;
    private SharedPreferences msharedPreferences;
    private JSONObject responseJson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msharedPreferences=getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, parent, false);
        muserName=v.findViewById(R.id.userName);

        musrAge=v.findViewById(R.id.userAge);
//        muserTel=v.findViewById(R.id.usertel);
        muserchecknum=v.findViewById(R.id.userchecknum);
        mchecknumBtn=v.findViewById(R.id.checknumbtn);

        muserSex=v.findViewById(R.id.sexradioGroup);
        RadioButton rMale = v.findViewById(R.id.radio0);
        RadioButton rFemale = v.findViewById(R.id.radio1);
        muserSex.setOnCheckedChangeListener(this);

        mchecknumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new checknumFetchTask().execute();
            }
        });

        mregisterBtn=v.findViewById(R.id.registerbtn);
        mregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new accountFetchTask().execute();
            }
        });
        return v;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio0:
                msharedPreferences.edit().putString("userSex", "男").apply();
                break;
            case R.id.radio1:
                msharedPreferences.edit().putString("userSex", "女").apply();
                break;
        }
    }

    private class checknumFetchTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                JSONObject jsonObject = new JSONObject();
//                        .put("userTel", muserTel.getText());
                responseJson = new NetConnetcion(getContext()).Post(UrlBase.BASE+"require_validation", jsonObject.toString());

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
                    Toast.makeText(getContext(), responseJson.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), responseJson.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class accountFetchTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                JSONObject jsonObject = new JSONObject()
                        .put("userName", muserName.getText())
                        .put("userAge", musrAge.getText())
//                        .put("userTel",muserTel.getText())
                        .put("validationCode",muserchecknum.getText())
                        .put("userSex",msharedPreferences.getString("userSex","无"));
                responseJson=new NetConnetcion(getContext()).Post(UrlBase.BASE+"register_user", jsonObject.toString());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return responseJson;
        }

        @Override
        protected void onPostExecute(JSONObject responseJson) {
            super.onPostExecute(responseJson);
            try {
                if(responseJson.getString("status").equals("ok")){
                    Toast.makeText(getContext(),"注册成功",Toast.LENGTH_SHORT).show();
//                    msharedPreferences.edit().putString("userTel",muserTel.getText().toString()).apply();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(),"验证码有误",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
