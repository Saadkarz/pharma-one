package se7ati.pharma.PharmAlert.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se7ati.pharma.PharmAlert.enums.Role;
import se7ati.pharma.PharmAlert.Bean.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByIdentifiant(String identifiant);
    
    List<Utilisateur> findByRole(Role role);
    
    List<Utilisateur> findByActifTrue();
}
