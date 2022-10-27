package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
@Component
public class AuthorDAOImpl implements AuthorDAO{
    private final DataSource dataSource;

    public AuthorDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Author> getById(Long id) {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Optional<Author> authorOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" select * from author where id=" + id);

            if (resultSet.next()) {
                Author author = new Author();
                author.setId(resultSet.getLong(1));
                author.setFirstName(resultSet.getString(2));
                author.setLastName(resultSet.getString(3));
                authorOptional=Optional.of(author);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                resultSet.close();

                statement.close();
                connection.close();


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

      return authorOptional;
    }

}
