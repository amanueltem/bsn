package com.aman.book_network.feedback;

import com.aman.book_network.book.Book;
import org.springframework.stereotype.Service;
@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(
                    Book.builder()
                        .id(request.bookId())
                        .archived(false)
                        .sharable(false)
                        .build()
                )
                .build();
    }
}
