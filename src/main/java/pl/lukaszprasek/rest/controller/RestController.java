package pl.lukaszprasek.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import pl.lukaszprasek.rest.model.BookEntity;
import pl.lukaszprasek.rest.model.Config;
import pl.lukaszprasek.rest.model.repositories.BookRepository;

import java.util.Optional;

@Controller
public class RestController {
    @Autowired
    BookRepository bookRepository;

    @GetMapping(value = "/book/{id}", produces = "application/json")
    public ResponseEntity getBook(@PathVariable("id") int id) {
        Optional<BookEntity> bookEntity = bookRepository.findById(id);
        return bookEntity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(value = "/booking/{title}", produces = "application/json")
    public ResponseEntity getBookByTitle(@PathVariable("title") String title) {
        boolean ifExists = bookRepository.existsByTitle(title);
        System.out.println("Znajduje"+ifExists);
        if (ifExists) {
            return ResponseEntity.ok(ifExists);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/book", produces = "application/json")
    public ResponseEntity getBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }
    //ALBO
    //return bookEntity.map(s->ResponseEntity.ok(s)).orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    //To samo co ponizej
//       if (!bookEntity.isPresent()){
//           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();//jezeli nie znajdzie wyrzuci bla 404, not found
//       }
//        return ResponseEntity.ok(bookEntity.get());//jak znajdzie ma nam dac obiekt
//    }


    @PostMapping(value = "/book", consumes = "application/json")
    public ResponseEntity createBook(@RequestHeader("key") String key, @RequestBody BookEntity bookEntity) {
        if (checkKey(key)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        //walidacja wszystkich pól
        if (bookRepository.existsByTitle(bookEntity.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        bookRepository.save(bookEntity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "book/{id}")
    public ResponseEntity deleteBook(@RequestHeader("key") String key, @PathVariable("id") int id) {
        if (checkKey(key)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private boolean checkKey(String key) {
        if (!key.equals(Config.API_KEY)) {
            return true;
        }
        return false;
    }

    @PutMapping(value = "/book", consumes = "application/json")
    public ResponseEntity editBook(@RequestHeader("key") String key, @RequestBody BookEntity bookEntity) {
        if (checkKey(key)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!bookRepository.existsById(bookEntity.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        bookRepository.save(bookEntity);
        return ResponseEntity.ok().build();
    }
}
