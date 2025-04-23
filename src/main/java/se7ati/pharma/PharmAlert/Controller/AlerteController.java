package se7ati.pharma.PharmAlert.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se7ati.pharma.PharmAlert.Bean.Alerte;
import se7ati.pharma.PharmAlert.Service.AlerteService;
import se7ati.pharma.PharmAlert.enums.TypeAlerte;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
public class AlerteController {

    @Autowired
    private AlerteService alerteService;

    /**
     * Récupérer toutes les alertes avec filtrage optionnel par type et état
     * @param type Type d'alerte (optionnel)
     * @param actives Si true, retourne uniquement les alertes non traitées
     * @return Liste des alertes correspondant aux critères
     */
    @GetMapping
    public ResponseEntity<List<Alerte>> getAlertes(
            @RequestParam(required = false) TypeAlerte type,
            @RequestParam(required = false, defaultValue = "false") boolean actives) {
        
        try {
            List<Alerte> alertes;
            
            if (type != null && actives) {
                // Filtrer par type et ne retourner que les alertes actives
                alertes = alerteService.getAlertesByType(type).stream()
                        .filter(alerte -> !alerte.isTraitee())
                        .toList();
            } else if (type != null) {
                // Filtrer uniquement par type
                alertes = alerteService.getAlertesByType(type);
            } else if (actives) {
                // Retourner uniquement les alertes actives
                alertes = alerteService.getAlertesActives();
            } else {
                // Retourner toutes les alertes
                alertes = alerteService.getAllAlertes();
            }
            
            return ResponseEntity.ok(alertes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marquer une alerte comme traitée
     * @param id Identifiant de l'alerte
     * @return L'alerte mise à jour
     */
    @PostMapping("/traiter/{id}")
    public ResponseEntity<Alerte> traiterAlerte(@PathVariable Long id) {
        try {
            Alerte alerteTraitee = alerteService.traiterAlerte(id);
            return ResponseEntity.ok(alerteTraitee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Forcer la génération des alertes (utile pour les tests ou l'administration)
     * @return Un message de confirmation
     */
    @PostMapping("/generer")
    public ResponseEntity<String> forcerGenerationAlertes() {
        try {
            alerteService.forceGenererAlertes();
            return ResponseEntity.ok("Génération d'alertes effectuée avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la génération des alertes: " + e.getMessage());
        }
    }
}