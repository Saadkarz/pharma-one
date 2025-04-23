package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se7ati.pharma.PharmAlert.Bean.Vente;

import java.time.LocalDateTime;
import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    
    List<Vente> findByUtilisateurId(Long utilisateurId);
    
    List<Vente> findByDateBetween(LocalDateTime debut, LocalDateTime fin);
    
    @Query("SELECT v FROM Vente v JOIN v.lignes l WHERE l.produit.id = :produitId")
    List<Vente> findByProduitId(@Param("produitId") Long produitId);
}
