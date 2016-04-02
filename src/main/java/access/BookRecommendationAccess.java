package access;

import java.util.List;

public interface BookRecommendationAccess {
    void bookReadingComplete(Long userId, Book book);
    List<Book> getReadBooks(Long userId);

    void addCurrentReadBook(Long userId, Book book);

    List<Book> getCommonBooksRead(Long userId1, Long user2);
}
