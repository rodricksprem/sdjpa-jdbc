package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
@Component
public class BookDAOImpl implements BookDAO{
    private final DataSource dataSource;
    private final AuthorDAO authorDAO;
    public BookDAOImpl(DataSource dataSource, AuthorDAO authorDAO) {
        this.dataSource = dataSource;
        this.authorDAO = authorDAO;
    }

    @Override
    public Optional<Book> getById(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Book> bookOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(" select id,isbn, publisher, title, author_id from book where id=?" );
            preparedStatement.setLong(1,id);
            resultSet = preparedStatement.executeQuery();
            bookOptional=this.getBook(resultSet);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return bookOptional;
    }

    @Override
    public Optional<Book> findBookByTitle(String title) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Book> bookOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(" select id,isbn, publisher, title, author_id from book where title=?" );
            preparedStatement.setString(1,title);
            resultSet = preparedStatement.executeQuery();
            bookOptional=this.getBook(resultSet);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return bookOptional;
    }

    @Override
    public Optional<Book> saveNewBook(Book book) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Book> bookOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("insert into book (isbn, publisher, title, author_id) values(?,?,?,?)" );
            preparedStatement.setString(1,book.getIsbn());
            preparedStatement.setString(2,book.getPublisher());
            preparedStatement.setString(3,book.getTitle());
            if(book.getAuthor()!=null)
            {
                preparedStatement.setLong(4,book.getAuthor().getId());
            }else{
                preparedStatement.setLong(4,-1);
            }
            preparedStatement.execute();
            statement = connection.createStatement();
            ResultSet rs=statement.executeQuery("select LAST_INSERT_ID()");
            if(rs.next()){
                Long savedId = rs.getLong(1);

                return this.getById(savedId);
            }



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

        return bookOptional;
    }

    @Override
    public Optional<Book> updateBook(Book book) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Optional<Book> bookOptional = Optional.empty();

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("update book set isbn=?, publisher=?, title=?, author_id=? where id=?" );
            preparedStatement.setString(1,book.getIsbn());
            preparedStatement.setString(2,book.getPublisher());
            preparedStatement.setString(3,book.getTitle());
            if(book.getAuthor()!=null) {
                preparedStatement.setLong(4, book.getAuthor().getId());
            }else{
                preparedStatement.setLong(4,-5);
            }
            preparedStatement.setLong(5,book.getId());

            preparedStatement.execute();
            statement = connection.createStatement();
            bookOptional=this.getById(book.getId());




        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.closeAll(connection,preparedStatement,resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return bookOptional;
    }

    @Override
    public void deleteBookById(Long id) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("Delete from book where id=?" );
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

    private Optional<Book> getBook(ResultSet resultSet) throws SQLException {
        Optional<Book> bookOptional = Optional.empty();
        if(resultSet.next()){
            Book book = new Book();
            book.setId(resultSet.getLong(1));
            //isbn, publisher, title, author_id
            book.setIsbn(resultSet.getString(2));
            book.setPublisher(resultSet.getString(3));
            book.setTitle(resultSet.getString(4));
            Long authorId= resultSet.getLong(5);
            Optional<Author> authorOptional = authorDAO.getById(authorId);
            if(authorOptional.isPresent())
            {
                book.setAuthor(authorOptional.get());
            }
            bookOptional=Optional.of(book);
        }
        return bookOptional;
    }

    private void closeAll(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
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
