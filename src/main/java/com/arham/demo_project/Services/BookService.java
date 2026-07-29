package com.arham.demo_project.Services;

import com.arham.demo_project.Model.Book;
import com.arham.demo_project.Repositry.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {

    @Autowired
    private BookRepository repo;

    public List<Book> getAllBooks(){
        List<Book> books = repo.findAll();
        if (books.isEmpty()) {
            throw new NoSuchElementException("No books found");
        }
        return books;
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
        info.setId(id);
        if(repo.validate(id)) {
            repo.save(info);
            return info;
        }else
            throw new NoSuchElementException("Book not found");
    }

    public Book delete(Long id){
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Book not found");
        }
        Book b=repo.getById(id);
        repo.deleteById(id);
        return b;
    }

    public boolean isavailable(Long id){
        return repo.isavailable(id);
    }

    public void increaseIssueQuantity(Long id){
        repo.increaseIssueQuantity(id);
    }


    public void decreaseIssueQuantity(Long id){
        repo.decreaseIssueQuantity(id);
    }
}
