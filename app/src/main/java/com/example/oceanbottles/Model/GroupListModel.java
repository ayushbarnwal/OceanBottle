package com.example.oceanbottles.Model;

public class GroupListModel {
    String groupName,groupProfilePic;

    public GroupListModel() {
    }

    public GroupListModel(String groupName, String groupProfilePic) {
        this.groupName = groupName;
        this.groupProfilePic = groupProfilePic;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupProfilePic() {
        return groupProfilePic;
    }

    public void setGroupProfilePic(String groupProfilePic) {
        this.groupProfilePic = groupProfilePic;
    }
}
