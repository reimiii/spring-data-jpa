package franxx.code.jpa.service;

import franxx.code.jpa.entity.Category;
import franxx.code.jpa.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private TransactionOperations transactionOperations;

  @Autowired
  private PlatformTransactionManager transactionManager;

  public void manual() {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setTimeout(10);
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus transaction = transactionManager.getTransaction(def);

    try {
      for (int i = 0; i < 5; i++) {
        Category category = new Category();
        category.setName("Category manual" + i);
        categoryRepository.save(category);
      }
      error();
      transactionManager.commit(transaction);
    } catch (Throwable throwable) {
      transactionManager.rollback(transaction);
      throw throwable;
    }
  }

  public void createCategoryTrans() {
    transactionOperations.executeWithoutResult(transactionStatus -> {
      for (int i = 0; i < 5; i++) {
        Category category = new Category();
        category.setName("Category " + i);
        categoryRepository.save(category);
      }

      error();
    });

  }

  public void error() {
    throw new RuntimeException("Error");
  }

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
