package com.arham.demo_project.Services;

import com.arham.demo_project.Model.Book;
import com.arham.demo_project.Repositry.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<Book> getAllBooks(){
        return repo.findAll();
    }

    public Book getBook(Long id){
        if(repo.validate(id))
            return repo.getById(id);
        else
            throw new NoSuchElementException("Book not found");
    }

    public Book addBook(Book b){
        repo.save(b);
        return b;
    }

    public Book edit(Long id, Book info){
        Book existing = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
        if(info.getTotal_quantity() < existing.getIssue_quantity())
            throw new ArithmeticException("Total_quantity can't be less than issued quantity");
        info.setId(id);
        info.setIssue_quantity(existing.getIssue_quantity());  // preserve the real loaned count
        repo.save(info);
        return info;
    }

    public Book delete(Long id){
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Book not found");
        }
        Book b=repo.getById(id);
        if(b.getIssue_quantity()>0)
            throw new IllegalArgumentException("Book can't be deleted,already issued to someone");
        repo.deleteById(id);
        return b;
    }

    // Fetches the book with a row lock; must be called inside a transaction.
    public Book getBookForUpdate(Long id){
        return repo.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "book is unavailable"));
    }
}
