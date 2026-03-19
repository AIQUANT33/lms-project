package com.example.demo.dto;

public class ProgressAnalytics {

    private int totalCourses;
    private int completedCourses;
    private int activeCourses;

    private long modulesCompleted;
    private long lessonsCompleted;

    private double averageScore;

    //  weekly learning activity (Mon-Sun)
    private int[] weeklyActivity;

    public ProgressAnalytics() {}

    public ProgressAnalytics(
            int totalCourses,
            int completedCourses,
            int activeCourses,
            long modulesCompleted,
            long lessonsCompleted,
            double averageScore,
            int[] weeklyActivity) {

        this.totalCourses = totalCourses;
        this.completedCourses = completedCourses;
        this.activeCourses = activeCourses;
        this.modulesCompleted = modulesCompleted;
        this.lessonsCompleted = lessonsCompleted;
        this.averageScore = averageScore;
        this.weeklyActivity = weeklyActivity;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public int getCompletedCourses() {
        return completedCourses;
    }

    public int getActiveCourses() {
        return activeCourses;
    }

    public long getModulesCompleted() {
        return modulesCompleted;
    }

    public long getLessonsCompleted() {
        return lessonsCompleted;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int[] getWeeklyActivity() {
        return weeklyActivity;
    }

    public void setWeeklyActivity(int[] weeklyActivity) {
        this.weeklyActivity = weeklyActivity;
    }
}