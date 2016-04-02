package access;

import lombok.Data;

import java.io.Serializable;

@Data
public class Book implements Serializable {
    private final Long id;
    private final String author;
    private final String ISBN;
}
