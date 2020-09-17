package com.boug.geospatial.repository;

import com.boug.geospatial.domain.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

    /**
     *  Official postgres documentation  https://www.postgresql.org/docs/9.5/transaction-iso.html
     *  Repeatable Read Isolation Level
     *  A repeatable read transaction cannot modify or lock rows changed by other transactions after the repeatable
     *  read transaction began.
     *  Applications using this level must be prepared to retry transactions due to serialization failures.
     *  if the first updater commits (and actually updated or deleted the row, not just locked it) then the
     *  repeatable read transaction will be rolled back with the message
     *  ERROR:  could not serialize access due to concurrent update
     *
     *  Spring JPA will throw org.springframework.dao.CannotAcquireLockException according to
     *  https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/dao/CannotAcquireLockException.html
     *  which is a runTime exception.
     *
     * @param id the id of the row that must be updated
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE point_of_interest SET request_counter = request_counter +1 " +
                   "WHERE id = :id" , nativeQuery = true)
    void increaseCounter(@Param("id") Long id);


}
