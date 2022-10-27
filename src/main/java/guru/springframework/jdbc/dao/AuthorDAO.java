package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface AuthorDAO {
    Optional<Author> getById(Long id);
    Optional<Author> getByName(String firstName,String lastName);

    Optional<Author> saveAutor(Author author);

    Optional<Author> updateAutor(Author author);

    void deleteAuthorById(Long id);


}
