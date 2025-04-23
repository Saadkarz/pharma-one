package se7ati.pharma.PharmAlert.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se7ati.pharma.PharmAlert.Bean.Produit;
import se7ati.pharma.PharmAlert.Service.ProduitService;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    /**
     * Récupère tous les produits
     * @return Liste de tous les produits
     */
    @GetMapping
    public ResponseEntity<List<Produit>> getAllProduits() {
        List<Produit> produits = produitService.getAllProduits();
        return ResponseEntity.ok(produits);
    }

    /**
     * Récupère un produit par son ID
     * @param id Identifiant du produit
     * @return Le produit correspondant ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        try {
            Produit produit = produitService.getProduitById(id);
            return ResponseEntity.ok(produit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crée un nouveau produit
     * @param produit Produit à créer
     * @return Le produit créé
     */
    @PostMapping
    public ResponseEntity<Produit> createProduit(@RequestBody Produit produit) {
        Produit nouveauProduit = produitService.createProduit(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauProduit);
    }

    /**
     * Met à jour un produit existant
     * @param id Identifiant du produit à mettre à jour
     * @param produitDetails Nouvelles informations du produit
     * @return Le produit mis à jour ou 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable Long id, @RequestBody Produit produitDetails) {
        try {
            Produit produitMisAJour = produitService.updateProduit(id, produitDetails);
            return ResponseEntity.ok(produitMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un produit
     * @param id Identifiant du produit à supprimer
     * @return 204 No Content si supprimé avec succès ou 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        try {
            produitService.deleteProduit(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}