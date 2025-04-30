package com.aman.book_network.feedback;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse{
	private double note;
	private String comment;
	private boolean ownFeedback;
}