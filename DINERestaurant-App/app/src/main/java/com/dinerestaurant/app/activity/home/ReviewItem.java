package com.dinerestaurant.app.activity.home;

public class ReviewItem {
    private String avatarPath;
    private String reviewerName;
    private String reviewDate;
    private int rating;
    private String reviewText;

    public ReviewItem(String avatarPath, String reviewerName, String reviewDate, int rating, String reviewText) {
        this.avatarPath = avatarPath;
        this.reviewerName = reviewerName;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}
