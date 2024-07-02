package franxx.code.jpa.repository;

import franxx.code.jpa.entity.Category;
import franxx.code.jpa.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductRepository productRepository;


  @Test
  void insertSomeCategory() {
    {
      Category category = new Category();
      category.setName("phone");
      categoryRepository.save(category);
      System.out.println(category.getId());
    }
    {
      Category category = new Category();
      category.setName("car");
      categoryRepository.save(category);
      System.out.println(category.getId());
    }
  }

  @Test
  void createProduct() {
    Category categoryPhone = categoryRepository.findById(22L).orElse(null);
    Category categoryCar = categoryRepository.findById(23L).orElse(null);
    assertNotNull(categoryPhone);
    assertNotNull(categoryCar);

    {
      Product product = new Product();
      product.setName("Apple Iphone 11");
      product.setPrice(2_000_000L);
      product.setCategory(categoryPhone);
      productRepository.save(product);
    }
    {
      Product product = new Product();
      product.setName("Apple Iphone 12");
      product.setPrice(3_000_000L);
      product.setCategory(categoryPhone);
      productRepository.save(product);
    }

    {
      Product product = new Product();
      product.setName("Toyota");
      product.setPrice(110_000_000L);
      product.setCategory(categoryCar);
      productRepository.save(product);
    }
    {
      Product product = new Product();
      product.setName("Mazda");
      product.setPrice(120_000_000L);
      product.setCategory(categoryCar);
      productRepository.save(product);
    }
  }

  @Test
  void findAllByCategories() {
    List<Product> phone = productRepository.findAllByCategory_Name("phone");
    List<Product> car = productRepository.findAllByCategory_Name("car");

    assertEquals(2, phone.size());
    assertEquals("Apple Iphone 11", phone.get(0).getName());
    assertEquals("Apple Iphone 12", phone.get(1).getName());

    assertEquals(2, car.size());
    assertEquals("Toyota", car.get(0).getName());
    assertEquals("Mazda", car.get(1).getName());
  }

  @Test
  void sort() {
    Sort sort = Sort.by(Sort.Order.desc("id"));
    List<Product> phone = productRepository.findAllByCategory_Name("phone", sort);
    List<Product> car = productRepository.findAllByCategory_Name("car");

    assertEquals(2, phone.size());
    assertEquals("Apple Iphone 11", phone.get(1).getName());
    assertEquals("Apple Iphone 12", phone.get(0).getName());

    assertEquals(2, car.size());
    assertEquals("Toyota", car.get(0).getName());
    assertEquals("Mazda", car.get(1).getName());
  }

  @Test
  void pageable() {
    // page 1
    Pageable pageable = PageRequest.of(
          0,
          1,
          Sort.by(Sort.Order.desc("price"))
    );

    List<Product> car = productRepository.findAllByCategory_Name("car", pageable);
    assertEquals(1, car.size());
    assertEquals("Mazda", car.getFirst().getName());

    // page 2
    pageable = PageRequest.of(
          1,
          1,
          Sort.by(Sort.Order.desc("price"))
    );

    car = productRepository.findAllByCategory_Name("car", pageable);
    assertEquals(1, car.size());
    assertEquals("Toyota", car.getFirst().getName());
  }
}