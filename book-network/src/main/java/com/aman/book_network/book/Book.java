package com.aman.book_network.book;
import java.beans.Transient;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aman.book_network.common.BaseEntity;
import com.aman.book_network.feedback.Feedback;
import com.aman.book_network.history.BookTransactionHistory;
import com.aman.book_network.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity{
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean sharable;
     @ManyToOne
     @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;
    @Transient
    public double getRate(){
        if(feedbacks==null||feedbacks.isEmpty()){
            return 0.0;
        }
        var rate=this.feedbacks.stream()
                  .mapToDouble(Feedback::getNote)
                  .average()
                  .orElse(0.0);
       double roundedRate=Math.round(rate*10.0)/10.0;
       return roundedRate;
    }
}
