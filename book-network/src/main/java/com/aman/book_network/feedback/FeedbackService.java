package com.aman.book_network.feedback;

import com.aman.book_network.book.Book;
import com.aman.book_network.book.BookRepository;
import com.aman.book_network.book.PageResponse;
import com.aman.book_network.user.User;
import com.aman.book_network.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the Id: " + request.bookId()));

        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("You cannot give feedback for archived or non-sharable books.");
        }

        User user = (User) authentication.getPrincipal();

        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book.");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }
	
	public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId,
	                                                               int page,
																   int size,
																   Autehtication connectedUser){
		Pageable pageable= PageRequest.of(page,size);
		User user=((user)connectedUser.getPrincipal());
		Page<Feedback> feedbacks=feedbackRepository.findAllByBookId(bookId,pageable);
		return null;															   
																   }
}
