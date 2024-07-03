package franxx.code.jpa.repository;

import franxx.code.jpa.entity.Category;
import franxx.code.jpa.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  List<Product> findAllByCategory_Name(String name);

  List<Product> findAllByCategory_Name(String name, Sort sort);

  Page<Product> findAllByCategory_Name(String name, Pageable pageable);

  Long countByCategory_Name(String name);

  Boolean existsByName(String name);

  @Transactional
  Integer deleteByName(String name);

  List<Product> searchProductUsingName(@Param("name") String name, Pageable pageable);

  @Query(
        value = "select p from Product p where p.name like :name or p.category.name like :name",
        countQuery = "select count(p) from Product p where p.name like :name or p.category.name like :name"
  )
  Page<Product> searchProduct(@Param("name") String name, Pageable pageable);

  @Modifying
  @Query("delete from Product p where p.name = :name")
  Integer deleteProductUsingName(@Param("name") String name);

  @Modifying
  @Query("update Product p set p.price = 0 where p.id = :id")
  Integer updateProductToZero(@Param("id") Long id);

  Stream<Product> streamAllByCategory(Category category);

  Slice<Product> findAllByCategory(Category category, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Product> findFirstByIdEquals(Long id);
}
