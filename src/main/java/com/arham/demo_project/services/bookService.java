package com.arham.demo_project.services;

import com.arham.demo_project.model.book;
import com.arham.demo_project.repositry.bookrRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class bookService {

    @Autowired
    private bookrRepositry repo;

    public List<book> getAllBooks(){
        List<book> books = repo.findAll();
        if (books.isEmpty()) {
            throw new NoSuchElementException("No books found");
        }
        return books;
    }

    public book getBook(Long id){
        if(repo.validate(id))
            return repo.getById(id);
        else
            throw new NoSuchElementException("Book not found");
    }

    public book addBook(book b){
        repo.save(b);
        return b;
    }

    public book edit(Long id,book info){
        info.setId(id);
        if(repo.validate(id)) {
            repo.save(info);
            return info;
        }else
            throw new NoSuchElementException("Book not found");
    }

    public book delete(Long id){
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Book not found");
        }
        book b=repo.getById(id);
        repo.deleteById(id);
        return b;
    }

    public boolean isavailable(Long id){
        return repo.isavailable(id);
    }

    public void increaseIssuequatity(Long id){
        repo.increaseissuequatity(id);
    }

    public void decreaseIssueQuatity(Long id){
        repo.decreaseissuequatity(id);
    }
}
