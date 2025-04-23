package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se7ati.pharma.PharmAlert.Bean.Fournisseur;

import java.util.List;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    
    List<Fournisseur> findByNomContainingIgnoreCase(String nom);
}
