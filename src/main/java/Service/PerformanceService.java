package Service;

import DAO.PerformanceDAO;
import Model.PerformanceReview;

import java.util.List;

public class PerformanceService {
    private final PerformanceDAO performanceDAO = new PerformanceDAO();

    public void submitReview(PerformanceReview review) {
        performanceDAO.submitReview(review);
    }

    public List< PerformanceReview