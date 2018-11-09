package edu.northeastern.ccs.im.MongoDB.Model;

import java.util.List;

public class Group {

    private int _id;
    private String name;
    private List<User> members;
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<User> getMembers() {
        return members;
    }
    public void setMembers(List<User> members) {
        this.members = members;
    }
    
    public void addMember(User member) {
        this.members.add(member);
    }
    
    public boolean removeMember(User member) {
        if(this.members.contains(member)) {
            members.remove(member);
            return true;
        }
        return false;
    }
}
