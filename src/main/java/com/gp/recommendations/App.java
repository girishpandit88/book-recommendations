package com.gp.recommendations;

import access.AccessModule;
import access.Book;
import access.BookRecommendationAccess;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.UUID;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Config c = ConfigFactory.load("application.conf");
                bind(Config.class).toInstance(c);
                install(new AccessModule());
            }
        });
        final BookRecommendationAccess _access = injector.getInstance(BookRecommendationAccess.class);

        _access.addCurrentReadBook(1L,new Book(12345L,"Girish", UUID.randomUUID().toString()));
        _access.bookReadingComplete(1l,new Book(12345L,"Girish", UUID.randomUUID().toString()));
        System.out.println(_access.getReadBooks(1l));
        System.out.println("Hello World!");
    }
}
