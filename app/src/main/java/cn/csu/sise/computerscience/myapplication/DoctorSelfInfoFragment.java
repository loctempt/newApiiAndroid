package cn.csu.sise.computerscience.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cn.csu.sise.computerscience.myapplication.alipay.PayDemoActivity;
import cn.csu.sise.computerscience.myapplication.alipay.PayResult;

import static cn.csu.sise.computerscience.myapplication.DoctorSelfInfoActivity.EXTRA_DOCTOR_ID;

public class DoctorSelfInfoFragment extends Fragment {
    public static final String TAG = "DoctorSelfInfoFragment";
    private ImageView mimg;
    private TextView mname;
    private TextView mdepartment;
    private TextView mpositionalTitle;
    private TextView mjob;
    private TextView mtel;
    private TextView mintroduction;
    private String doctorId;
    private JSONObject responseJsonInfo;
    private JSONObject responseJsonSchedule;
    private JSONObject responseJsonLogInStaus;
    private RecyclerView mReservationRecyclerView;
    private String scheduleId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorId = getActivity().getIntent().getStringExtra(EXTRA_DOCTOR_ID);
        new DoctorSelfInfoFetchTask().execute();
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_self_info, parent, false);

//        mimg = v.findViewById(R.id.img);
        mname = v.findViewById(R.id.name);
        mdepartment = v.findViewById(R.id.department);
        mpositionalTitle = v.findViewById(R.id.positionalTitle);
        mjob = v.findViewById(R.id.job);
        mtel = v.findViewById(R.id.tel);
        mintroduction = v.findViewById(R.id.introduction);
        mReservationRecyclerView = v.findViewById(R.id.select_reservation_recycle_list_view);

        mReservationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mReservationRecyclerView.addItemDecoration(divider);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.detail_title);

        return v;
    }


    private class DoctorSelfInfoFetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONObject jsonObject = new JSONObject()
                        .put("doctorId", doctorId);
                responseJsonInfo = new NetConnetcion(getContext()).Post(UrlBase.BASE + "information/doctor_information", jsonObject.toString());
                responseJsonSchedule = new NetConnetcion(getContext()).Post(UrlBase.BASE + "information/doctor_schedule", jsonObject.toString());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (responseJsonInfo.getString("status").equals("ok") && responseJsonSchedule.getString("status").equals("ok")) {
                    Log.d(TAG, "onPostExecute: 信息" + responseJsonInfo);
                    Log.d(TAG, "onPostExecute: 列表" + responseJsonSchedule);
                    JSONObject docPersonnalInfo = responseJsonInfo.getJSONObject("extra");
                    mjob.setText(docPersonnalInfo.getString("doctorJob"));
                    mdepartment.setText(docPersonnalInfo.getString("doctorDepartment"));
                    mname.setText(docPersonnalInfo.getString("doctorName"));
                    mpositionalTitle.setText(docPersonnalInfo.getString("doctorPositionalTitle"));
                    mtel.setText(docPersonnalInfo.getString("doctorTel"));
                    mintroduction.setText(docPersonnalInfo.getString("doctorIntroduction"));

                    Doctor doctor = new Doctor();
                    doctor.doctorSchedules = doctor.parseDoctorSchedule(responseJsonSchedule);
                    mReservationRecyclerView.setAdapter(new reservationAdapter(doctor));
//                todo imageview 医生头像没有处理
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class reservationHolder extends RecyclerView.ViewHolder {
        TextView mReservationTime;
        Button mReservationButton;
        Doctor.DoctorSchedule mDoctorSchedule;

        public reservationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_reserve, parent, false));
            mReservationTime = itemView.findViewById(R.id.reserve_time);
            mReservationButton = itemView.findViewById(R.id.reservationbtn);
        }

        public void bind(Doctor.DoctorSchedule doctorSchedule) {
            mDoctorSchedule = doctorSchedule;
            mReservationTime.setText(mDoctorSchedule.doctorOnDutyDate + mDoctorSchedule.doctorOnDutyTime);
            mReservationButton.setEnabled(mDoctorSchedule.available);
            if (!mDoctorSchedule.available) {
                mReservationButton.setBackground(getContext().getDrawable(R.drawable.shape_disabled));
                mReservationButton.setTextColor(getContext().getColor(R.color.white));
                mReservationButton.setText("预约已满");
            }
            mReservationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scheduleId = mDoctorSchedule.scheduleId;
                    new pingTask().execute();
                }
            });
        }
    }

    private class pingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                responseJsonLogInStaus = new NetConnetcion(getContext()).Post(UrlBase.BASE + "ping", "");
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
                if (responseJsonLogInStaus.getString("status").equals("ok")) {
                    PayDemoActivity.payV2(getActivity(), new Handler() {
                        @SuppressWarnings("unused")
                        public void handleMessage(Message msg) {
                            @SuppressWarnings("unchecked")
                            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                            String resultStatus = payResult.getResultStatus();
                            if (TextUtils.equals(resultStatus, "9000")) {
                                Log.d(TAG, "handleMessage: 支付成功");
                                new makeReservationTask().execute();
                            } else {
                                Log.d(TAG, "handleMessage: 支付失败");
                            }
                        }
                    });
                }
                else{
                    startActivity(new Intent(getContext(), LogInActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class makeReservationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                responseJsonLogInStaus = new NetConnetcion(getContext()).Post(UrlBase.BASE + "data_alter/new_reservation", "{\"scheduleId\":"+scheduleId+"}");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (responseJsonInfo.getString("status").equals("ok")) {
                    Toast.makeText(getContext(), "预约成功", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class reservationAdapter extends RecyclerView.Adapter<reservationHolder> {

        private Doctor mDoctor;

        public reservationAdapter(Doctor doctor) {
            this.mDoctor = doctor;
        }

        @NonNull
        @Override
        public reservationHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new reservationHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull reservationHolder reservationHolder, int i) {
            reservationHolder.bind(mDoctor.doctorSchedules.get(i));
        }

        @Override
        public int getItemCount() {
            return mDoctor.doctorSchedules.size();
        }
    }
}
