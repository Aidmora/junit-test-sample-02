package epn.edu.ec.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import epn.edu.ec.exception.CakeNotFoundException;
import epn.edu.ec.model.cake.CakeResponse;
import epn.edu.ec.model.cake.CakesResponse;
import epn.edu.ec.model.cake.CreateCakeRequest;
import epn.edu.ec.model.cake.UpdateCakeRequest;
import epn.edu.ec.service.CakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/cakes")
public class CakeController {
        private final CakeService cakeService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public CakesResponse getCakes() {
        log.info("getting all cakes");

        return cakeService.getCakes();
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CakeResponse> getCakeById(@PathVariable long id) {
        log.info("getting cake with id {}", id);

        return ResponseEntity.ok(cakeService.getCakeById(id));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public CakeResponse createCake(@RequestBody CreateCakeRequest createCakeRequest) {
        log.info("creating cake {}", createCakeRequest);

        CakeResponse cake = cakeService.createCake(createCakeRequest);

        log.info("cake created, cake id {}", cake.getId());

        return cake;
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CakeResponse> updateCake(@PathVariable long id, @RequestBody UpdateCakeRequest updateCakeRequest) {
        log.info("updating cake with id {}: {}", id, updateCakeRequest);

        CakeResponse updated = cakeService.updateCake(id, updateCakeRequest);

        log.info("cake updated, cake id {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCake(@PathVariable long id) {
        log.info("deleting cake with id {}", id);

        cakeService.deleteCake(id);

        log.info("cake deleted, cake id {}", id);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @ExceptionHandler(CakeNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    private void cakeNotFoundException() {
    }
    
}
