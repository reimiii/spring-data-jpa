package franxx.code.jpa.repository;

import franxx.code.jpa.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findAllByCategory_Name(String name);

  List<Product> findAllByCategory_Name(String name, Sort sort);
}
