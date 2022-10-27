package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
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
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Author> authorOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(" select * from author where id=?" );
            preparedStatement.setLong(1,id);
            resultSet = preparedStatement.executeQuery();
            authorOptional=this.getAutor(resultSet);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

      return authorOptional;
    }

    @Override
    public Optional<Author> getByName(String firstName, String lastName) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Author> authorOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(" select * from author where first_name like ? and last_name like ?" );
            preparedStatement.setString(1,firstName);
            preparedStatement.setString(2,lastName);
            resultSet = preparedStatement.executeQuery();
            authorOptional=this.getAutor(resultSet);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return authorOptional;
    }

    @Override
    public Optional<Author> saveAutor(Author author) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Author> authorOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("insert into author (first_name,last_name) values(?,?)" );
            preparedStatement.setString(1,author.getFirstName());
            preparedStatement.setString(2,author.getLastName());
            preparedStatement.execute();
            statement = connection.createStatement();
            ResultSet rs=statement.executeQuery("select LAST_INSERT_ID()");
            if(rs.next()){
                Long savedId = rs.getLong(1);

                return this.getById(savedId);
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return authorOptional;
    }

    @Override
    public Optional<Author> updateAutor(Author author) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Author> authorOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            System.out.println(author.toString());
            preparedStatement = connection.prepareStatement("update author set first_name=? , last_name=? where author.id=?" );
            preparedStatement.setString(1,author.getFirstName());
            preparedStatement.setString(2,author.getLastName());
            preparedStatement.setLong(3,author.getId());

            preparedStatement.execute();
            authorOptional=this.getById(author.getId());



        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return authorOptional;
    }

    @Override
    public void deleteAuthorById(Long id) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("Delete from author where id=?" );
            preparedStatement.setLong(1,id);
            preparedStatement.execute();



        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }



    }

    private Optional<Author> getAutor(ResultSet resultSet) throws SQLException {
       Optional<Author> authorOptional = Optional.empty();
        if(resultSet.next()){
            Author author = new Author();
            author.setId(resultSet.getLong(1));
            author.setFirstName(resultSet.getString(2));
            author.setLastName(resultSet.getString(3));
            authorOptional=Optional.of(author);
        }
        return authorOptional;
    }

    private void closeAll(Connection connection,PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
}
