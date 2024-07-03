package franxx.code.jpa.repository;

import franxx.code.jpa.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  void queryMethod() {

    Category category = categoryRepository.findFirstByNameEquals("test upd").orElse(null);
    assertNotNull(category);
    assertEquals("test upd", category.getName());

    List<Category> allByNameLike = categoryRepository.findAllByNameLike("%test%");
    assertEquals(1, allByNameLike.size());
    assertEquals("test upd", allByNameLike.getFirst().getName());
  }

  @Test
  void insert() {
    Category category = new Category();
    category.setName("test");
    categoryRepository.save(category);
    assertNotNull(category.getId());
  }

  @Test
  void update() {
    Category category = categoryRepository.findById(1L).orElse(null);
    assertNotNull(category);

    category.setName("test upd");
    categoryRepository.save(category);

    category = categoryRepository.findById(1L).orElse(null);
    assertNotNull(category);
    assertEquals("test upd", category.getName());

//    categoryRepository.delete(category);
  }

  @Test
  void audit() {
    Category category = new Category();
    category.setName("tank");
    categoryRepository.save(category);

    assertNotNull(category.getId());
    assertNotNull(category.getCreatedDate());
    assertNotNull(category.getLastModifiedDate());
  }
}