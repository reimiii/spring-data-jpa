package franxx.code.jpa.repository;

import franxx.code.jpa.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

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
}