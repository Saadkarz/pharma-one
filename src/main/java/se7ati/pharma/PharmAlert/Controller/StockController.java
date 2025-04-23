package se7ati.pharma.PharmAlert.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se7ati.pharma.PharmAlert.Bean.StockMouvement;
import se7ati.pharma.PharmAlert.Service.StockService;

import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * Endpoint pour enregistrer une réception de stock
     * @param mouvement Les détails du mouvement de stock
     * @return Le mouvement de stock enregistré
     */
    @PostMapping("/reception")
    public ResponseEntity<StockMouvement> enregistrerReception(@RequestBody StockMouvement mouvement) {
        try {
            StockMouvement result = stockService.enregistrerReception(mouvement);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour enregistrer une sortie de stock (perte, retour)
     * @param mouvement Les détails du mouvement de stock
     * @return Le mouvement de stock enregistré
     */
    @PostMapping("/sortie")
    public ResponseEntity<StockMouvement> enregistrerSortie(@RequestBody StockMouvement mouvement) {
        try {
            StockMouvement result = stockService.enregistrerSortie(mouvement);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour consulter l'état actuel de tout le stock
     * @return L'état du stock pour tous les produits
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEtatStock() {
        try {
            Map<String, Object> result = stockService.getEtatStock();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour consulter l'état du stock d'un produit spécifique
     * @param produitId L'identifiant du produit
     * @return L'état du stock pour ce produit
     */
    @GetMapping("/produit/{produitId}")
    public ResponseEntity<Map<String, Object>> getEtatStockProduit(@PathVariable Long produitId) {
        try {
            Map<String, Object> result = stockService.getEtatStockProduit(produitId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}