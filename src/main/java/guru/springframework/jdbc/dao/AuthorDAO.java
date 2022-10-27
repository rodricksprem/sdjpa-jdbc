package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;

import java.util.Optional;

public interface AuthorDAO {
    Optional<Author> getById(Long id);
    Optional<Author> getByName(String firstName,String lastName);

}
