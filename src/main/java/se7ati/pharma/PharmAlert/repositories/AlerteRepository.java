package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se7ati.pharma.PharmAlert.enums.TypeAlerte;
import se7ati.pharma.PharmAlert.Bean.Alerte;

import java.util.List;

public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    
    List<Alerte> findByTraiteeFalse();
    
    List<Alerte> findByType(TypeAlerte type);
    
    List<Alerte> findByProduitId(Long produitId);
    
    List<Alerte> findByLotId(Long lotId);
}
