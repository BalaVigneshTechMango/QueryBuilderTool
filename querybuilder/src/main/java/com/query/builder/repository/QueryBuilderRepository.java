//package com.query.builder.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface QueryBuilderRepository extends JpaRepository<Object, Integer> {
//
//	@Query(value = "SELECT mallName FROM mallproject", nativeQuery = true)
//	List<Object[]> getDataFromDatabase();
//
//}
