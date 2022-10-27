package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

import java.util.Optional;

public interface BookDAO {
    Optional<Book> getById(Long id);

    Optional<Book> findBookByTitle(String title);

    Optional<Book> saveNewBook(Book book);

    Optional<Book> updateBook(Book book);

    void deleteBookById(Long id);
}
