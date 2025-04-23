package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se7ati.pharma.PharmAlert.enums.TypeMouvement;
import se7ati.pharma.PharmAlert.Bean.StockMouvement;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMouvementRepository extends JpaRepository<StockMouvement, Long> {
    
    List<StockMouvement> findByLotId(Long lotId);
    
    List<StockMouvement> findByType(TypeMouvement type);
    
    List<StockMouvement> findByDateBetween(LocalDateTime debut, LocalDateTime fin);
    
    @Query("SELECT m FROM StockMouvement m WHERE m.lot.produit.id = :produitId")
    List<StockMouvement> findByProduitId(@Param("produitId") Long produitId);
}
