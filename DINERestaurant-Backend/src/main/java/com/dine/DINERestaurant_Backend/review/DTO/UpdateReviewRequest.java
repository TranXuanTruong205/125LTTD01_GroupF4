package com.dine.DINERestaurant_Backend.review.DTO;

public class UpdateReviewRequest {

    private Integer rating;   // có thể null nếu chỉ sửa comment
    private String comment;   // có thể null nếu chỉ sửa rating

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
