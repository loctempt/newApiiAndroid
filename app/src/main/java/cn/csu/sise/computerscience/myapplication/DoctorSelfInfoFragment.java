package cn.csu.sise.computerscience.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.detail_title);

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
            if(!mDoctorSchedule.available){
                mReservationButton.setBackground(getContext().getDrawable(R.drawable.shape_disabled));
                mReservationButton.setTextColor(getContext().getColor(R.color.white));
                mReservationButton.setText("预约已满");
            }
            mReservationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scheduleId = mDoctorSchedule.scheduleId;
                    new logInStatusFetchTask().execute();
//                    todo 在跳转前加判断 是否登录 登录才可预约
                }
            });
        }
    }

    private class logInStatusFetchTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                responseJsonLogInStaus = new NetConnetcion(getContext()).Post(UrlBase.BASE + "ping", "");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (responseJsonInfo.getString("status").equals("ok") ) {
                    startActivity(ReservationRecordActivity.getIntent(getContext(), scheduleId));
//                    todo 接入sdk 付款
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
