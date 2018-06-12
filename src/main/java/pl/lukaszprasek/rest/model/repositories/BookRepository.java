package pl.lukaszprasek.rest.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.lukaszprasek.rest.model.BookEntity;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, Integer> {
    boolean existsByTitle (String title);
    boolean existsById (int id);
}
