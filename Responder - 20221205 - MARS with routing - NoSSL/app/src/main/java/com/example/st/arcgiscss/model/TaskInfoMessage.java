package com.example.st.arcgiscss.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskInfoMessage implements Serializable {
	
    private String incidentId;
    private ArrayList<TaskInfo> taskInfo;
    private String userId;
    private String createTime;
    private String taskId;
    
    
	public TaskInfoMessage() {
		
	}	
        
    public TaskInfoMessage(JSONObject json) {
    
        this.incidentId = json.optString("incident_id");

        this.taskInfo = new ArrayList<TaskInfo>();
        JSONArray arrayTaskInfo = json.optJSONArray("task_info");
        if (null != arrayTaskInfo) {
            int taskInfoLength = arrayTaskInfo.length();
            for (int i = 0; i < taskInfoLength; i++) {
                JSONObject item = arrayTaskInfo.optJSONObject(i);
                if (null != item) {
                    this.taskInfo.add(new TaskInfo(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("task_info");
            if (null != item) {
                this.taskInfo.add(new TaskInfo(item));
            }
        }

        this.userId = json.optString("user_id");
        this.createTime = json.optString("create_time");
        this.taskId = json.optString("task_id");

    }
    
    public String getIncidentId() {
        return this.incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public ArrayList<TaskInfo> getTaskInfo() {
        return this.taskInfo;
    }

    public void setTaskInfo(ArrayList<TaskInfo> taskInfo) {
        this.taskInfo = taskInfo;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "TaskInfoMessage{" +
                "incidentId='" + incidentId + '\'' +
                ", taskInfo=" + taskInfo +
                ", userId='" + userId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
