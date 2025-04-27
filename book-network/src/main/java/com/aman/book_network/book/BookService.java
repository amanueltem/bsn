package com.aman.book_network.book;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aman.book_network.exception.OperationNotPermittedException;
import com.aman.book_network.file.FieStorageService;
import com.aman.book_network.history.BookTransactionHistory;
import com.aman.book_network.history.BookTransactionHistoryRepository;
import com.aman.book_network.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FieStorageService fileStorageService;
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        Book book=bookMapper.toBook(request);
        book.setOwner(user);
            return bookRepository.save(book).getId();
    }
    public BookResponse findById(Integer bookId) {
       return bookRepository.findById(bookId)
              .map(bookMapper::toBookResponse)
              .orElseThrow(()-> new EntityNotFoundException("No book found with  the ID:: "+bookId));
    }
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        Pageable pageable=PageRequest.of(page
        ,size,
        Sort.by("createdDate").descending());
        Page<Book> books=bookRepository.findAllDisplayableBooks(pageable,user.getId());
         List<BookResponse> bookResponse=books.stream()
                                         .map(bookMapper::toBookResponse)
                                         .toList();
        return new PageResponse<>(
            bookResponse,
            books.getNumber()
            ,books.getSize()
            ,books.getTotalElements()
            ,books.getTotalPages()
            ,books.isFirst()
            ,books.isLast()
        );
    }
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        Pageable pageable=PageRequest.of(page
        ,size,
        Sort.by("createdDate").descending());
        Page<Book> books=bookRepository.findAll
        (BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponse=books.stream()
                                         .map(bookMapper::toBookResponse)
                                         .toList();
        return new PageResponse<>(
            bookResponse,
            books.getNumber()
            ,books.getSize()
            ,books.getTotalElements()
            ,books.getTotalPages()
            ,books.isFirst()
            ,books.isLast()
        );
    }
    public  PageResponse<BorrowedBookResponse> findAllBorrowedBooks(
        int page, 
        int size, Authentication connectedUser) {
            User user=(User) connectedUser.getPrincipal();
            Pageable pageable=PageRequest.of(page
            ,size,
            Sort.by("createdDate").descending());

        Page<BookTransactionHistory> allBorrowedBooks=
        transactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());  

        List<BorrowedBookResponse> bookResponse=allBorrowedBooks.stream()
                                                .map(bookMapper::toBorrowedBookResponse)
                                                .toList();

                                                return new PageResponse<>(
                                                    bookResponse,
                                                    allBorrowedBooks.getNumber()
                                                    ,allBorrowedBooks.getSize()
                                                    ,allBorrowedBooks.getTotalElements()
                                                    ,allBorrowedBooks.getTotalPages()
                                                    ,allBorrowedBooks.isFirst()
                                                    ,allBorrowedBooks.isLast()
                                                );
    }
    public  PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        Pageable pageable=PageRequest.of(page
        ,size,
        Sort.by("createdDate").descending());

    Page<BookTransactionHistory> allBorrowedBooks=
    transactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());  

    List<BorrowedBookResponse> bookResponse=allBorrowedBooks.stream()
                                            .map(bookMapper::toBorrowedBookResponse)
                                            .toList();

                                            return new PageResponse<>(
                                                bookResponse,
                                                allBorrowedBooks.getNumber()
                                                ,allBorrowedBooks.getSize()
                                                ,allBorrowedBooks.getTotalElements()
                                                ,allBorrowedBooks.getTotalPages()
                                                ,allBorrowedBooks.isFirst()
                                                ,allBorrowedBooks.isLast()
                                            );
    }
    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId)
                    .orElseThrow(()->new EntityNotFoundException("No book foudn with ID::"+bookId));
        User user=(User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You can not update books sharable status.");
        }        
        book.setSharable(!book.isSharable());
        bookRepository.save(book);
       return book.getId();
    }
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
      Book book=bookRepository.findById(bookId).
      orElseThrow(()->new EntityNotFoundException("No book found with ID:: "+bookId));

      if(book.isArchived()|| !book.isSharable()){
        throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shearble.");

      }

     User user=((User)connectedUser.getPrincipal());
     if(Objects.equals(book.getOwner().getId(),user.getId())){
        throw new OperationNotPermittedException("You can not borrow your own book.");
     }

     final boolean isAlreadyBorrowed=transactionHistoryRepository
       .isAlreadyBorrowedByUser(bookId,user.getId());

    if(isAlreadyBorrowed){
        throw new OperationNotPermittedException("The requested book is already borrowed.");
    }
    BookTransactionHistory bookTransactionHistory=
       BookTransactionHistory.builder()
       .user(user)
       .book(book)
       .returned(false)
       .returnApproved(false)
       .build();
       return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
    public Integer returnBorrwedBook(Integer bookId, Authentication connectedUser) {
     
          Book book=bookRepository.findById(bookId).
      orElseThrow(()->new EntityNotFoundException("No book found with ID:: "+bookId));

      if(book.isArchived()|| !book.isSharable()){
        throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shearble.");

      }
      User user=((User)connectedUser.getPrincipal());
     if(Objects.equals(book.getOwner().getId(),user.getId())){
        throw new OperationNotPermittedException("You can not borrow or return your own book.");
     }


     BookTransactionHistory bookTransactionHistory= transactionHistoryRepository
                                                       .findByBookIdAndUserId(bookId,user.getId())
                                                       .orElseThrow(
                                                        ()-> new OperationNotPermittedException("You did not borrow this book")
                                                       );
     bookTransactionHistory.setReturned(true);
     return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
    public Integer approveReturnBorrwedBook(Integer bookId, Authentication connectedUser) {


          Book book=bookRepository.findById(bookId).
      orElseThrow(()->new EntityNotFoundException("No book found with ID:: "+bookId));

      if(book.isArchived()|| !book.isSharable()){
        throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shearble.");

      }
      User user=((User)connectedUser.getPrincipal());
     if(Objects.equals(book.getOwner().getId(),user.getId())){
        throw new OperationNotPermittedException("You can not borrow or return your own book.");
     }


      BookTransactionHistory bookTransactionHistory= transactionHistoryRepository
                                                       .findByBookIdAndOwnerId(bookId,user.getId())
                                                       .orElseThrow(
                                                        ()-> new OperationNotPermittedException("The book is not returened.")
                                                       );

             bookTransactionHistory.setReturnApproved(true);                                          

      return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        
         Book book=bookRepository.findById(bookId).
      orElseThrow(()->new EntityNotFoundException("No book found with ID:: "+bookId));

         User user=((User)connectedUser.getPrincipal());
          var bookCover=fileStorageService.saveFile(file,book,user.getId());
          book.setBookCover(bookCover);
          bookRepository.save(book);
    }

}
