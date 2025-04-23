package se7ati.pharma.PharmAlert.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se7ati.pharma.PharmAlert.Bean.Vente;
import se7ati.pharma.PharmAlert.Service.VenteService;

import java.util.List;

@RestController
@RequestMapping("/api/ventes")
public class VenteController {

    @Autowired
    private VenteService venteService;

    /**
     * Créer une nouvelle vente
     * @param vente Les données de la vente à créer
     * @return La vente créée
     */
    @PostMapping
    public ResponseEntity<Vente> creerVente(@RequestBody Vente vente) {
        try {
            Vente nouvelleVente = venteService.creerVente(vente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleVente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer la liste des ventes récentes (30 derniers jours)
     * @return Liste des ventes récentes
     */
    @GetMapping
    public ResponseEntity<List<Vente>> getVentesRecentes() {
        try {
            List<Vente> ventes = venteService.getVentesRecentes();
            return ResponseEntity.ok(ventes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer toutes les ventes
     * @return Liste de toutes les ventes
     */
    @GetMapping("/all")
    public ResponseEntity<List<Vente>> getAllVentes() {
        try {
            List<Vente> ventes = venteService.getAllVentes();
            return ResponseEntity.ok(ventes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les détails d'une vente spécifique
     * @param id L'identifiant de la vente
     * @return Les détails de la vente
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vente> getVenteById(@PathVariable Long id) {
        try {
            Vente vente = venteService.getVenteById(id);
            return ResponseEntity.ok(vente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les ventes d'un produit spécifique
     * @param produitId L'identifiant du produit
     * @return Liste des ventes contenant ce produit
     */
    @GetMapping("/produit/{produitId}")
    public ResponseEntity<List<Vente>> getVentesByProduitId(@PathVariable Long produitId) {
        try {
            List<Vente> ventes = venteService.getVentesByProduitId(produitId);
            return ResponseEntity.ok(ventes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}