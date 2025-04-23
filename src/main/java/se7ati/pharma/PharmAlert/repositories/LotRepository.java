package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se7ati.pharma.PharmAlert.Bean.Lot;

import java.time.LocalDate;
import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {
    
    List<Lot> findByProduitId(Long produitId);
    
    @Query("SELECT l FROM Lot l WHERE l.produit.id = :produitId AND l.quantite > 0 ORDER BY l.dateExpiration ASC")
    List<Lot> findByProduitIdWithStock(@Param("produitId") Long produitId);
    
    @Query("SELECT l FROM Lot l WHERE l.dateExpiration < :date AND l.quantite > 0")
    List<Lot> findLotsExpiringBefore(@Param("date") LocalDate date);
    
    @Query("SELECT l FROM Lot l WHERE l.dateExpiration BETWEEN :startDate AND :endDate AND l.quantite > 0")
    List<Lot> findLotsExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
