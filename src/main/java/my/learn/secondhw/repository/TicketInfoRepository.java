package my.learn.secondhw.repository;

import my.learn.secondhw.model.TicketInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface TicketInfoRepository extends JpaRepository<TicketInfo, UUID> {
    @Transactional
    @Modifying
    @Query(
            "UPDATE TicketInfo i SET " +
                    "i.price = :price, " +
                    "i.currency = :currency, " +
                    "i.availability = :availability " +
                    "WHERE i.id = :id")
    void updateById(
            @Param("price") BigDecimal price,
            @Param("currency") String currency,
            @Param("availability") Boolean availability,
            @Param("id") UUID id);
}
