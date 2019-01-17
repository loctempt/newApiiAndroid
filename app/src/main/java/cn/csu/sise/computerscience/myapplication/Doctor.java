package cn.csu.sise.computerscience.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Doctor {
    public String doctorId;
    public String doctorName;
    public String doctorDepartment;
    public String doctorJob;
    public String doctorPositionalTitle;
    public String doctorTel;
    public String doctorIntroduction;

    public class DoctorSchedule {
        public DoctorSchedule(String scheduleId, String doctorOnDutyDate, String doctorOnDutyTime, boolean available) {
            this.scheduleId = scheduleId;
            this.doctorOnDutyDate = doctorOnDutyDate;
            this.doctorOnDutyTime = doctorOnDutyTime;
            this.available = available;
        }

        public String scheduleId;
        public String doctorOnDutyDate;
        public String doctorOnDutyTime;
        public boolean available;
    }

    public List<DoctorSchedule> doctorSchedules;

    public List<DoctorSchedule> parseDoctorSchedule(JSONObject jsonObject) throws JSONException {
        JSONArray extra = jsonObject.getJSONArray("extra");
        ArrayList<DoctorSchedule> list = new ArrayList<>();
        // 遍历JSONArray，取出其中的属性，添加到List中
        for (int i = 0; i < extra.length(); i++) {
            JSONObject jsonDoctorSchedule = extra.getJSONObject(i);
            DoctorSchedule doctorSchedule = new DoctorSchedule(jsonDoctorSchedule.getString("scheduleId"),
                    jsonDoctorSchedule.getString("doctorOnDutyDate"),
                    jsonDoctorSchedule.getString("doctorOnDutyTime"),
                    jsonDoctorSchedule.getBoolean("available")
            );
            list.add(doctorSchedule);
        }
        return list;
    }
}
