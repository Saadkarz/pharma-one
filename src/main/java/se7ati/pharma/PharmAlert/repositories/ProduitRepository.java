package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se7ati.pharma.PharmAlert.Bean.Produit;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    List<Produit> findByActifTrue();
    
    List<Produit> findByDesignationContainingIgnoreCase(String designation);
    
    List<Produit> findByCodeEAN(String codeEAN);
    
    List<Produit> findByCategorie(String categorie);
    
    @Query("SELECT p FROM Produit p JOIN p.lots l GROUP BY p HAVING SUM(l.quantite) <= p.seuilStockMin")
    List<Produit> findProduitsWithLowStock();
}
