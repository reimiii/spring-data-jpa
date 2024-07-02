package franxx.code.jpa.service;

import franxx.code.jpa.entity.Category;
import franxx.code.jpa.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepository categoryRepository;

  @Transactional
  public void createCategory() {
    for (int i = 0; i < 5; i++) {
      Category category = new Category();
      category.setName("Category " + i);
      categoryRepository.save(category);
    }

    throw new RuntimeException("ups rollback");
  }

  public void test() {
    createCategory();
  }
}
