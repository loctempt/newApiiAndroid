package cn.csu.sise.computerscience.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorListFragment extends Fragment {
    private static final String TAG = "DoctorListFragment";
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private RecyclerView mDepartmentRecyclerView;
    private RecyclerView mDoctorRecyclerView;
    private Toolbar mToolbar;
    private JSONObject mDepartmentsJson;
    private JSONObject mDoctorsJson;
    private JSONObject mLogoutJson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        sharedPreferences = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_doctor_list, container, false);
        mDepartmentRecyclerView = v.findViewById(R.id.rv_recyclerview_one);
        mDoctorRecyclerView = v.findViewById(R.id.rv_recyclerview_two);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        mDepartmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDepartmentRecyclerView.addItemDecoration(divider);
        mDoctorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDoctorRecyclerView.addItemDecoration(divider);

        new DepartmentsFetchTask().execute();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(getContext().getString(R.string.main_title));

        return v;
    }



    private class DoctorBrief {
        public DoctorBrief(String doctorId, String doctorJob, String doctorName, String doctorPositionalTitle ,String Expert) {
            this.doctorId = doctorId;
            this.doctorJob = doctorJob;
            this.doctorName = doctorName;
            this.doctorPositionalTitle = doctorPositionalTitle;
            this.Expert=Expert;
        }
        String Expert;
        String doctorId;
        String doctorJob;
        String doctorName;
        String doctorPositionalTitle;
    }

    private class DepartmentsFetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d(TAG, "doInBackground: 请求科室列表");
                mDepartmentsJson = new NetConnetcion(getContext()).Post(UrlBase.BASE + "information/department_list", "");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.d(TAG, "onPostExecute: " + mDepartmentsJson.toString());
                if (mDepartmentsJson.getString("status").equals("ok")) {
                    ArrayList<String> departments = new ArrayList<>();
                    JSONArray extra = mDepartmentsJson.getJSONArray("extra");
                    for (int i = 0; i < extra.length(); i++) {
                        Log.d(TAG, "onPostExecute: 取出的元素 " + extra.getString(i));
                        departments.add(extra.getString(i));
                    }
                    mDepartmentRecyclerView.setAdapter(new DepartmentAdapter(departments));
                    mDepartmentRecyclerView.getAdapter().notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DoctorsFetchTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                Log.d(TAG, "doInBackground: 请求医生列表");
                mDoctorsJson = new NetConnetcion(getContext()).Post(UrlBase.BASE + "information/doctor_information_brief", "{\"doctorDepartment\":\"" + params[0] + "\"}");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.d(TAG, "onPostExecute: " + mDoctorsJson.toString());
                if (mDoctorsJson.getString("status").equals("ok")) {
                    ArrayList<DoctorBrief> doctorBriefs = new ArrayList<>();
                    JSONArray extra = mDoctorsJson.getJSONArray("extra");
                    for (int i = 0; i < extra.length(); i++) {
                        Log.d(TAG, "onPostExecute: 取出的元素 " + extra.getJSONObject(i).toString());
                        JSONObject doctorBriefJson = extra.getJSONObject(i);
                        doctorBriefs.add(new DoctorBrief(
                                doctorBriefJson.getString("doctorId"),
                                doctorBriefJson.getString("doctorJob"),
                                doctorBriefJson.getString("doctorName"),
                                doctorBriefJson.getString("doctorPositionalTitle"),
                                doctorBriefJson.getString("doctorExpert")
//                                todo 后端加doctorExpert字段
                        ));
                    }
                    mDoctorRecyclerView.setAdapter(new DoctorBriefAdapter(doctorBriefs));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DepartmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mDepartmentNameTextView;
        String mDepartmentName;

        public DepartmentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_department, parent, false));
            mDepartmentNameTextView = itemView.findViewById(R.id.department_name);
            itemView.setOnClickListener(this);
        }

        public void bind(String departmentName) {
            mDepartmentName = departmentName;
            mDepartmentNameTextView.setText(mDepartmentName);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + mDepartmentName);
            new DoctorsFetchTask().execute(mDepartmentName);
        }
    }

    private class DepartmentAdapter extends RecyclerView.Adapter<DepartmentHolder> {

        private List<String> mDepartmentNames;

        public DepartmentAdapter(List<String> departmentNames) {
            mDepartmentNames = departmentNames;
        }

        @NonNull
        @Override
        public DepartmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new DepartmentHolder(inflater, parent);
        }


        @Override
        public void onBindViewHolder(@NonNull DepartmentHolder departmentHolder, int i) {
            Log.d(TAG, "onBindViewHolder: i=" + i);
            departmentHolder.bind(mDepartmentNames.get(i));
        }

        @Override
        public int getItemCount() {
            return mDepartmentNames.size();
        }
    }

    private class DoctorBriefHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mDoctorNameTextView;
        TextView mDoctorPositionalTitleTextView;
        TextView mDoctorJobTextView;
        TextView mExpertTextView;
        DoctorBrief mDoctorBrief;

        public DoctorBriefHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_doctor_brief, parent, false));
            mDoctorJobTextView = itemView.findViewById(R.id.doctor_job);
            mDoctorNameTextView = itemView.findViewById(R.id.doctor_name);
            mDoctorPositionalTitleTextView = itemView.findViewById(R.id.positional_title);
            mExpertTextView = itemView.findViewById(R.id.Expert);
            itemView.setOnClickListener(this);
        }

        public void bind(DoctorBrief doctorBrief) {
            mDoctorBrief = doctorBrief;
            mDoctorJobTextView.setText(mDoctorBrief.doctorJob);
            mDoctorPositionalTitleTextView.setText(mDoctorBrief.doctorPositionalTitle);
            mDoctorNameTextView.setText(mDoctorBrief.doctorName);
            mExpertTextView.setText(mDoctorBrief.Expert);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "右边 onClick: ");
            String doctorId = mDoctorBrief.doctorId;
            startActivity(DoctorSelfInfoActivity.getIntent(getContext(), doctorId));
        }
    }

    private class DoctorBriefAdapter extends RecyclerView.Adapter<DoctorBriefHolder> {

        private List<DoctorBrief> mDoctorBriefs;

        public DoctorBriefAdapter(List<DoctorBrief> doctorBriefs) {
            mDoctorBriefs = doctorBriefs;
        }

        @NonNull
        @Override
        public DoctorBriefHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new DoctorBriefHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorBriefHolder doctorBriefHolder, int i) {
            doctorBriefHolder.bind(mDoctorBriefs.get(i));
        }

        @Override
        public int getItemCount() {
            return mDoctorBriefs.size();
        }
    }
}
