package com.aman.book_network.feedback;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.aman.book_network.book.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("feedbacks") // optional: include /api for best practice
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }
	@GetMapping("/book/{book-id}")
	public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
	@PathVariable("book-id") Integer bookId,
	@RequestParam(name="page", defaultValue="0",required=false)int page,
	@RequestParam(name="size", defaultValue="10",required=false)int size,
	Authentication connectedUser
	){
		return ResponseEntity.ok(service.findAllFeedbacksByBook(bookId,page,size,connectedUser));
}

}
