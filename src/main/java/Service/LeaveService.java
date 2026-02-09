package Service;

import DAO.LeaveDAO;
import Model.Leave;

import java.util.List;

public class LeaveService {
    private final LeaveDAO leaveDAO = new LeaveDAO();

    public void applyLeave(Leave leave) {
        leaveDAO.applyLeave(leave);
    }

    public List<Leave> getLeavesByEmployee(int empId) {
        return leaveDAO.getLeavesByEmployee(empId);
    }

    public void approveLeave(int leaveId, String comment) {
        leaveDAO.approveLeave(leaveId, comment);
    }
}