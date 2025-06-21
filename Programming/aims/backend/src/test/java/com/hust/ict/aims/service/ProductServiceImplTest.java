//package com.hust.ict.aims.service;
//
//import com.hust.ict.aims.model.Book;
//import com.hust.ict.aims.model.Product;
//import com.hust.ict.aims.repository.ProductRepository;
//import com.hust.ict.aims.service.impl.ProductServiceImpl;
//import jakarta.persistence.EntityManager;
//import org.hibernate.proxy.HibernateProxy;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//import org.hibernate.proxy.HibernateProxy;
//import org.hibernate.proxy.LazyInitializer;
//
//class ProductServiceImplTest {
//
//    @Test
//    void whenFindByIdWithProxy_thenReturnUnproxiedEntity() {
//        // Tạo mock EntityManager
//        EntityManager entityManager = Mockito.mock(EntityManager.class);
//        ProductRepository repository = Mockito.mock(ProductRepository.class);
//        ProductServiceImpl service = new ProductServiceImpl(repository);
//        service.entityManager = entityManager;
//
//        // Tạo proxy giả lập cho Book
//        Book bookProxy = Mockito.mock(Book.class,
//                Mockito.withSettings().extraInterfaces(HibernateProxy.class));
//        HibernateProxy proxy = (HibernateProxy) bookProxy;
//
//        // Tạo mock cho LazyInitializer
//        LazyInitializer li = Mockito.mock(LazyInitializer.class);
//        Book realBook = new Book();
//        realBook.setId(1L);
//
//        // Stub cả hai phương thức
//        when(li.getImplementation()).thenReturn(realBook);
//        when(proxy.getHibernateLazyInitializer()).thenReturn(li);
//
//        // Khi gọi find, trả về proxy
//        when(entityManager.find(Product.class, 1L))
//                .thenReturn(bookProxy);
//
//        // Gọi service
//        Product result = service.findById(1L);
//
//        // Assert
//        assertNotSame(bookProxy, result);
//        assertEquals(realBook.getId(), result.getId());
//    }
//}