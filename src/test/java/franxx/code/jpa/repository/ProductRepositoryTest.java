package franxx.code.jpa.repository;

import franxx.code.jpa.entity.Category;
import franxx.code.jpa.entity.Product;
import franxx.code.jpa.model.ProductPrice;
import franxx.code.jpa.model.SimpleProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private TransactionOperations transactionOperations;

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

    Page<Product> car = productRepository.findAllByCategory_Name("car", pageable);
    assertEquals(1, car.getContent().size());
    assertEquals(0, car.getNumber());
    assertEquals(2, car.getTotalElements());
    assertEquals(2, car.getTotalPages());
    assertEquals("Mazda", car.getContent().getFirst().getName());

    // page 2
    pageable = PageRequest.of(
          1,
          1,
          Sort.by(Sort.Order.desc("price"))
    );

    car = productRepository.findAllByCategory_Name("car", pageable);
    assertEquals(1, car.getContent().size());
    assertEquals(1, car.getNumber());
    assertEquals(2, car.getTotalElements());
    assertEquals(2, car.getTotalPages());
    assertEquals("Toyota", car.getContent().getFirst().getName());

  }

  @Test
  void count() {
    Long count = productRepository.count();
    assertEquals(4, count);

    count = productRepository.countByCategory_Name("phone");
    assertEquals(2, count);
  }

  @Test
  void testExist() {
    Boolean toyota = productRepository.existsByName("Toyota");
    assertTrue(toyota);
    Boolean mazda = productRepository.existsByName("Mazda");
    assertTrue(mazda);
  }

  @Test
  void deleteTestOld() {
    transactionOperations.executeWithoutResult(transactionStatus -> { // transaction 1
      Category byId = categoryRepository.findById(23L).orElse(null);
      assertNotNull(byId);

      Product product = new Product();
      product.setName("Subaru");
      product.setPrice(221_000_000L);
      product.setCategory(byId);
      productRepository.save(product); // transaction 1

      Integer subaru = productRepository.deleteByName("Subaru");// transaction 1
      assertEquals(1, subaru);
      subaru = productRepository.deleteByName("Subaru");// transaction 1
      assertEquals(0, subaru);
    });
  }

  @Test
  void deleteTestNew() {
    Category byId = categoryRepository.findById(23L).orElse(null);
    assertNotNull(byId);

    Product product = new Product();
    product.setName("Subaru");
    product.setPrice(221_000_000L);
    product.setCategory(byId);
    productRepository.save(product); // transaction 1

    Integer subaru = productRepository.deleteByName("Subaru");// transaction 2
    assertEquals(1, subaru);
    subaru = productRepository.deleteByName("Subaru");// transaction 3
    assertEquals(0, subaru);
  }

  @Test
  void namedQuery() {
    Pageable pageable = PageRequest.of(0, 1);
    List<Product> products = productRepository.searchProductUsingName("Toyota", pageable);
    assertEquals(1, products.size());
    assertEquals("Toyota", products.getFirst().getName());
  }

  @Test
  void searchProduct() {
    Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
    Page<Product> products = productRepository.searchProduct("%Apple%", pageable);
    assertEquals(1, products.getContent().size());

    products = productRepository.searchProduct("%car%", pageable);
    assertEquals(1, products.getContent().size());
  }

  @Test
  void modifying() {
    transactionOperations.executeWithoutResult(transactionStatus -> {
      Integer total = productRepository.deleteProductUsingName("Wrong");
      assertEquals(0, total);

      total = productRepository.updateProductToZero(1L);
      assertEquals(1, total);

      Product product = productRepository.findById(1L).orElse(null);
      assertNotNull(product);
      assertEquals(0L, product.getPrice());
    });
  }

  @Test
  void stream() {
    transactionOperations.executeWithoutResult(transactionStatus -> {
      Stream<Product> productStream = productRepository.streamAllByCategory(
            categoryRepository.findById(23L).orElse(null)
      );
      productStream.forEach(p -> {
        System.out.println(p.getId() + ": " + p.getName() + " - " + p.getCategory().getName());
      });
    });
  }

  @Test
  void slice() {
    Pageable pageable = PageRequest.of(0, 1);
    Category category = categoryRepository.findById(23L).orElse(null);
    assertNotNull(category);
    Slice<Product> allByCategory = productRepository.findAllByCategory(category, pageable);

    while (allByCategory.hasNext()) {
      allByCategory = productRepository.findAllByCategory(category, allByCategory.nextPageable());
    }

  }

  @Test
  void lock1() {
    transactionOperations.executeWithoutResult(transactionStatus -> {
      try {
        Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
        assertNotNull(product);

        product.setPrice(22_000_000L);
        Thread.sleep(20_000L);
        productRepository.save(product);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Test
  void lock2() {
    transactionOperations.executeWithoutResult(transactionStatus -> {

      Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
      assertNotNull(product);

      product.setPrice(100_000_000L);
      productRepository.save(product);
    });

  }

  @Test
  void specification() {
    Specification<Product> specification = (root, criteriaQuery, criteriaBuilder) -> {
      return criteriaQuery.where(
            criteriaBuilder.or(
                  criteriaBuilder.equal(root.get("name"), "Toyota"),
                  criteriaBuilder.equal(root.get("name"), "Mazda")
            )
      ).getRestriction();
    };

    List<Product> all = productRepository.findAll(specification);
    assertEquals(2, all.size());
  }

  @Test
  void projection() {
    List<SimpleProduct> allByNameLike = productRepository.findAllByNameLike("%Apple%", SimpleProduct.class);
    assertEquals(2, allByNameLike.size());

    List<ProductPrice> allByNameLikePrice = productRepository.findAllByNameLike("%Apple%", ProductPrice.class);
    assertEquals(2, allByNameLikePrice.size());
  }
}