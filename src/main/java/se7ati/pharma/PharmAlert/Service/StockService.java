package se7ati.pharma.PharmAlert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se7ati.pharma.PharmAlert.Bean.Lot;
import se7ati.pharma.PharmAlert.Bean.Produit;
import se7ati.pharma.PharmAlert.Bean.StockMouvement;
import se7ati.pharma.PharmAlert.Bean.Utilisateur;
import se7ati.pharma.PharmAlert.enums.TypeMouvement;
import se7ati.pharma.PharmAlert.repositories.LotRepository;
import se7ati.pharma.PharmAlert.repositories.ProduitRepository;
import se7ati.pharma.PharmAlert.repositories.StockMouvementRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StockMouvementRepository stockMouvementRepository;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private ProduitRepository produitRepository;

    /**
     * Enregistre une réception de produits en stock
     * @param mouvement Détails du mouvement de stock
     * @return Le mouvement de stock enregistré
     */
    @Transactional
    public StockMouvement enregistrerReception(StockMouvement mouvement) {
        // Vérification du type de mouvement
        if (mouvement.getType() != TypeMouvement.RECEPTION) {
            throw new IllegalArgumentException("Le type de mouvement doit être RECEPTION");
        }
        
        // Mise à jour de la date si non fournie
        if (mouvement.getDate() == null) {
            mouvement.setDate(LocalDateTime.now());
        }
        
        // Mise à jour de la quantité du lot
        Lot lot = mouvement.getLot();
        Integer quantiteActuelle = lot.getQuantite() != null ? lot.getQuantite() : 0;
        lot.setQuantite(quantiteActuelle + mouvement.getQuantite());
        lotRepository.save(lot);
        
        // Enregistrement du mouvement
        return stockMouvementRepository.save(mouvement);
    }

    /**
     * Enregistre une sortie de produits du stock (perte, retour, etc.)
     * @param mouvement Détails du mouvement de stock
     * @return Le mouvement de stock enregistré
     */
    @Transactional
    public StockMouvement enregistrerSortie(StockMouvement mouvement) {
        // Vérification du type de mouvement
        if (mouvement.getType() != TypeMouvement.PERTE && mouvement.getType() != TypeMouvement.RETOUR) {
            throw new IllegalArgumentException("Le type de mouvement doit être PERTE ou RETOUR");
        }
        
        // Vérification que le motif est fourni
        if (mouvement.getMotif() == null || mouvement.getMotif().trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif est obligatoire pour une sortie de stock");
        }
        
        // Mise à jour de la date si non fournie
        if (mouvement.getDate() == null) {
            mouvement.setDate(LocalDateTime.now());
        }
        
        // Vérification et mise à jour de la quantité du lot
        Lot lot = mouvement.getLot();
        Integer quantiteActuelle = lot.getQuantite() != null ? lot.getQuantite() : 0;
        
        if (quantiteActuelle < mouvement.getQuantite()) {
            throw new IllegalArgumentException("Quantité insuffisante en stock");
        }
        
        lot.setQuantite(quantiteActuelle - mouvement.getQuantite());
        lotRepository.save(lot);
        
        // Enregistrement du mouvement avec quantité négative
        mouvement.setQuantite(-Math.abs(mouvement.getQuantite())); // Assure que la quantité est négative
        return stockMouvementRepository.save(mouvement);
    }

    /**
     * Récupère l'état actuel du stock par produit et par lot
     * @return Une map contenant l'état du stock
     */
    public Map<String, Object> getEtatStock() {
        Map<String, Object> resultat = new HashMap<>();
        
        // Récupérer tous les produits
        List<Produit> produits = produitRepository.findAll();
        
        // Construire l'état du stock
        List<Map<String, Object>> stockProduits = produits.stream().map(produit -> {
            Map<String, Object> stockProduit = new HashMap<>();
            stockProduit.put("produit", produit);
            
            // Récupérer tous les lots avec stock > 0 pour ce produit
            List<Lot> lots = lotRepository.findByProduitIdWithStock(produit.getId());
            stockProduit.put("lots", lots);
            
            // Calculer le stock total pour ce produit
            int stockTotal = lots.stream()
                    .mapToInt(lot -> lot.getQuantite() != null ? lot.getQuantite() : 0)
                    .sum();
            stockProduit.put("quantiteTotal", stockTotal);
            
            return stockProduit;
        }).collect(Collectors.toList());
        
        resultat.put("stock", stockProduits);
        return resultat;
    }

    /**
     * Récupère l'état du stock pour un produit spécifique
     * @param produitId ID du produit
     * @return Une map contenant l'état du stock pour ce produit
     */
    public Map<String, Object> getEtatStockProduit(Long produitId) {
        Map<String, Object> resultat = new HashMap<>();
        
        // Récupérer le produit
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id " + produitId));
        
        resultat.put("produit", produit);
        
        // Récupérer tous les lots avec stock > 0 pour ce produit
        List<Lot> lots = lotRepository.findByProduitIdWithStock(produitId);
        resultat.put("lots", lots);
        
        // Calculer le stock total pour ce produit
        int stockTotal = lots.stream()
                .mapToInt(lot -> lot.getQuantite() != null ? lot.getQuantite() : 0)
                .sum();
        resultat.put("quantiteTotal", stockTotal);
        
        return resultat;
    }
}