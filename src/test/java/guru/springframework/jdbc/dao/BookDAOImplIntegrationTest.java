package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"guru.springframework.jdbc.dao"})
class BookDAOImplIntegrationTest {
    @Autowired
    BookDAO bookDAO;
    @Autowired
    AuthorDAO authorDAO;

    @Test
    void getById() {
        Optional<Book> bookOptional = bookDAO.getById(1L);
        if (bookOptional.isPresent()) {
            assertEquals(bookOptional.get().getId(), 1);
        }
    }

    @Test
    void findBookByTitle() {
        Optional<Book> bookOptional = bookDAO.findBookByTitle("Spring in Action, 6th Edition");
        if (bookOptional.isPresent()) {
            assertEquals(bookOptional.get().getId(), 3);
        }
    }

    @Test
    void saveNewBook() {
        Book book = new Book();
        book.setIsbn("1235");
        book.setPublisher("Self1");
        book.setTitle("my book1");

        Author author = new Author();
        author.setId(5L);

        book.setAuthor(author);
        Optional<Book> savedOptional = bookDAO.saveNewBook(book);
        if (savedOptional.isPresent()) {
            Book saved = savedOptional.get();
            assertThat(saved.getTitle()).isEqualTo("my book1");
        }


    }

    @Test
    void updateBook() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");

        Author author = new Author();
        author.setId(3L);

        book.setAuthor(author);

        Optional<Book> savedOptional = bookDAO.saveNewBook(book);

        Book saved = savedOptional.get();
        saved.setTitle("New Book");
        Optional<Book> updateBookOptional = bookDAO.updateBook(saved);
        Optional<Book> fetched = bookDAO.getById(updateBookOptional.get().getId());

        assertThat(fetched.get().getTitle()).isEqualTo("New Book1");
    }


    @Test
    void deleteBookById() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");
        Optional<Book> saved = bookDAO.saveNewBook(book);
        if(saved.isPresent()) {
            bookDAO.deleteBookById(saved.get().getId());
            Optional<Book> deleted = bookDAO.getById(saved.get().getId());
            assertThat(deleted).isEqualTo(Optional.empty());
        }



    }
}