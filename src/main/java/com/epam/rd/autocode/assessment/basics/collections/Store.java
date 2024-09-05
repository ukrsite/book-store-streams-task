package com.epam.rd.autocode.assessment.basics.collections;

import com.epam.rd.autocode.assessment.basics.entity.Book;
import com.epam.rd.autocode.assessment.basics.entity.Client;
import com.epam.rd.autocode.assessment.basics.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Store implements Add, Sort, Find {
    List<Order> orders;
    List<Book> books;
    List<Client> clients;

    @Override
    public void addBook(Book book) {
        if (book == null) {
            throw new NullPointerException("The Book object cannot be null.");
        }

        if (books.contains(book)) {
            throw new IllegalArgumentException("This book already exists in the collection.");
        }

        books.add(book);
    }

    @Override
    public void addOrder(Order order) {
        if (order == null) {
            throw new NullPointerException("The Order object cannot be null.");
        }

        orders.add(order);
    }

    @Override
    public Set<String> findAuthors() {
        if (books == null) {
            return Collections.emptySet();
        }

        Set<String> author = books.stream().map(Book::getAuthor).collect(Collectors.toSet());

        return author;
    }

    @Override
    public Map<String, List<Order>> findOrdersGroupedByClientId() {
        if (clients == null) {
            return Collections.emptyMap();
        }

        // Returns a map in which the key is the clientId
        // and the value is a list of the orders of selected client that the store has
        return clients.stream()
                .collect(Collectors.toMap(
                        client -> String.valueOf(client.getId()),  // Key: clientId as String
                        client -> orders.stream()                 // Value: List of orders for this client
                                .filter(order -> order.getClientId() == client.getId())
                                .collect(Collectors.toList())
                ));
    }

    @Override
    public List<String> findMostPopularAuthors() {
        if (books == null) {
            return Collections.emptyList();
        }

        // Calculates the number of books that have been ordered for each author
        Map<String, Integer> authorOrderCount = books.stream()
                .collect(Collectors.toMap(
                        Book::getAuthor,  // Key: Author
                        book -> orders.stream()  // Value: Sum of ordered books for that author
                                .filter(order -> order.getBookId() == book.getId())
                                .mapToInt(Order::getNumberOfBooks)
                                .sum(),
                        Integer::sum  // In case of duplicate authors, sum their counts
                ));

        // Return the list of authors sorted by the number of orders in descending order
        return authorOrderCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    @Override
    public List<Book> findBooksWhichPublishedAfterSelectedDate(LocalDate date) {
        if (books == null) {
            return Collections.emptyList();
        }

        // Returns a list of books whose published date is after the date parameter
        return books.stream()
                .filter(book -> book.getPublicationDate().isAfter(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksInPriceRange(BigDecimal min, BigDecimal max) {
        if (books == null) {
            return Collections.emptyList();
        }

        // Returns a list of books whose price is within the range provided by min and max parameters
        return books.stream()
                .filter(book -> min.compareTo(book.getPrice()) <= 0 && max.compareTo(book.getPrice()) >= 0)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Client> findClientsWithAveragePriceNoLessThan(List<Client> clients, int average) {
        if (clients == null || orders == null) {
            return Collections.emptySet();
        }

        // Stream through the clients, calculate the average order amount, and filter them
        return clients.stream()
                .filter(client -> {
                    // Find the orders of the current client
                    List<Order> clientOrders = orders.stream()
                            .filter(order -> order.getClientId() == client.getId())
                            .collect(Collectors.toList());

                    // Calculate total amount and average price for the client
                    int totalAmount = clientOrders.stream()
                            .mapToInt(order -> order.getPrice().intValue())
                            .sum();

                    int count = clientOrders.size();
                    int averageOrderAmount = count > 0 ? totalAmount / count : 0;

                    // Return true if the client's average order amount is not less than the specified average
                    return averageOrderAmount >= average;
                })
                .collect(Collectors.toSet());  // Collect the clients into a set
    }

    @Override
    public Set<Order> findOrdersByDate(LocalDateTime dateTime) {
        if (orders == null) {
            return Collections.emptySet();
        }

        // Stream through the orders and filter by the orderDate matching the dateTime parameter
        return orders.stream()
                .filter(order -> order.getOrderDate().isEqual(dateTime))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Order> sortOrdersByClientId() {
        if (orders == null) {
            return Collections.emptyList();
        }

        // Create a copy of the orders list to sort
        List<Order> sortedOrders = new ArrayList<>(orders);

        // Sort the list using the Comparator for clientId
        sortedOrders.sort(Comparator.comparingLong(Order::getClientId));

        return sortedOrders;
    }

    @Override
    public List<Book> sortBooksByPublishedYear() {
        if (books == null) {
            return Collections.emptyList();
        }

        // Create a copy of the books list to sort
        List<Book> sortedBooks = new ArrayList<>(books);

        // Sort the list using the Comparator for publication date
        sortedBooks.sort(Comparator.comparing(Book::getPublicationDate));

        return sortedBooks;
    }

    @Override
    public List<Book> sortBooksByPriceDesc() {
        if (books == null) {
            return Collections.emptyList();
        }

        // Create a copy of the books list to sort
        List<Book> sortedBooks = new ArrayList<>(books);

        // Sort the list by price in descending order
        sortedBooks.sort(Comparator.comparing(Book::getPrice).reversed());

        return sortedBooks;
    }
}
