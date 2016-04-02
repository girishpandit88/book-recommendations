package access.impl;

import access.Book;
import access.BookRecommendationAccess;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class BookRecommendationAccessImpl implements BookRecommendationAccess {
    private final GraphTraversalSource g;

    @Inject
    public BookRecommendationAccessImpl(TitanGraph graph) {
        this.g = graph.traversal();
    }

    @Override
    public void bookReadingComplete(Long userId, Book book) {
        final Vertex uVertex = get("id", userId);
        final Vertex bookVertex = get("bookId", book.getId());
        checkNotNull(uVertex,"user not found");
        checkNotNull(bookVertex,"book no found");
        g.V(uVertex).bothE("reading").toStream().forEach(edge -> {
            if(edge.inVertex().equals(bookVertex)){
                edge.remove();
            }
        });
        g.V(bookVertex).bothE("reading").toStream().forEach(edge -> {
            if(edge.outVertex().equals(uVertex)){
                edge.remove();
            }
        });
        uVertex.addEdge("read",bookVertex);
        bookVertex.addEdge("read",uVertex);
    }

    @Override
    public List<Book> getReadBooks(Long userId) {
        final List<Book> books = Lists.newArrayList();
        final Vertex uVertex = get("id", userId);
        g.V(uVertex).bothE("read").toList().stream().forEach(edge -> books.add((Book) edge.property("bookName").value()));
        return null;
    }

    @Override
    public void addCurrentReadBook(Long userId, Book book) {
        final Vertex uVertex = get("id", userId) == null ? createVertex(userId) : get("id", userId);
        final Vertex bookVertex = get("bookId", book.getId()) == null ? createBookVertex(book) : get("bookId", book.getId());
        uVertex.addEdge("reading", bookVertex);
        bookVertex.addEdge("reading", uVertex);
        g.tx().commit();
    }

    @Override
    public List<Book> getCommonBooksRead(Long userId1, Long userId2) {
        List<Book> commonBooks = Lists.newArrayList();
        final Vertex u1Vertex = get("id", userId1);
        final Vertex u2Vertex = get("id", userId2);
        final Set<Edge> booksReadByUser1 = g.V(u1Vertex).bothE("read").toSet();
        final Set<Edge> booksReadByUser2 = g.V(u2Vertex).bothE("read").toSet();
        booksReadByUser1.retainAll(booksReadByUser2);
        booksReadByUser1.forEach(edge -> {
            commonBooks.add((Book) edge.outVertex().property("bookId").value());
        });
        return commonBooks;
    }

    private Vertex get(String key, Long id) {
        final Iterator<Vertex> it = g.getGraph().get().traversal().V().has(key, id);
        Vertex v = it.hasNext() ? it.next() : null;
        it.forEachRemaining(vertex -> System.out.println(vertex.id()));
        return v;
    }


    private Vertex createBookVertex(Book book) {
        final Vertex vertex = g.getGraph().get().addVertex("bookId", book.getId());
        g.tx().commit();
        return vertex;
    }

    private Vertex createVertex(Long id) {
        final Vertex vertex = g.getGraph().get().addVertex();
        vertex.property("id", id);
        g.tx().commit();
        return vertex;
    }
}
