package se7ati.pharma.PharmAlert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se7ati.pharma.PharmAlert.Bean.Lot;
import se7ati.pharma.PharmAlert.Bean.StockMouvement;
import se7ati.pharma.PharmAlert.Bean.Vente;
import se7ati.pharma.PharmAlert.Bean.VenteLigne;
import se7ati.pharma.PharmAlert.enums.TypeMouvement;
import se7ati.pharma.PharmAlert.repositories.LotRepository;
import se7ati.pharma.PharmAlert.repositories.StockMouvementRepository;
import se7ati.pharma.PharmAlert.repositories.VenteRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VenteService {

    @Autowired
    private VenteRepository venteRepository;

    @Autowired
    private LotRepository lotRepository;
    
    @Autowired
    private StockMouvementRepository stockMouvementRepository;

    /**
     * Récupère toutes les ventes
     * @return Liste de toutes les ventes
     */
    public List<Vente> getAllVentes() {
        return venteRepository.findAll();
    }

    /**
     * Récupère une vente par son ID
     * @param id L'identifiant de la vente
     * @return La vente correspondante
     */
    public Vente getVenteById(Long id) {
        return venteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'id " + id));
    }

    /**
     * Crée une nouvelle vente et applique la stratégie FEFO pour le stock
     * @param vente Les détails de la vente à créer
     * @return La vente créée
     */
    @Transactional
    public Vente creerVente(Vente vente) {
        // Initialisation de la date si non fournie
        if (vente.getDate() == null) {
            vente.setDate(LocalDateTime.now());
        }
        
        // Calcul du total TTC si non fourni
        if (vente.getTotalTTC() == null) {
            BigDecimal total = BigDecimal.ZERO;
            for (VenteLigne ligne : vente.getLignes()) {
                if (ligne.getPrixUnitaireTTC() != null && ligne.getQuantite() != null) {
                    total = total.add(ligne.getPrixUnitaireTTC().multiply(new BigDecimal(ligne.getQuantite())));
                }
            }
            vente.setTotalTTC(total);
        }
        
        // Associer chaque ligne à cette vente et appliquer FEFO
        for (VenteLigne ligne : vente.getLignes()) {
            ligne.setVente(vente);
            
            // Appliquer la stratégie FEFO (First Expired, First Out)
            appliquerFEFO(ligne);
        }
        
        // Enregistrer la vente
        return venteRepository.save(vente);
    }
    
    /**
     * Applique la stratégie FEFO (First Expired First Out) pour décrémenter le stock
     * @param ligne La ligne de vente à traiter
     */
    private void appliquerFEFO(VenteLigne ligne) {
        int quantiteRestante = ligne.getQuantite();
        
        // Récupérer les lots disponibles du produit, triés par date d'expiration (FEFO)
        List<Lot> lots = lotRepository.findByProduitIdWithStock(ligne.getProduit().getId());
        
        if (lots.isEmpty()) {
            throw new RuntimeException("Aucun lot disponible pour le produit " + ligne.getProduit().getDesignation());
        }
        
        // Décrémenter le stock à partir des lots expirant le plus tôt
        for (Lot lot : lots) {
            if (quantiteRestante <= 0) {
                break;
            }
            
            int quantiteLot = lot.getQuantite() != null ? lot.getQuantite() : 0;
            int quantitePrelevee = Math.min(quantiteLot, quantiteRestante);
            
            if (quantitePrelevee > 0) {
                // Mise à jour du stock du lot
                lot.setQuantite(quantiteLot - quantitePrelevee);
                lotRepository.save(lot);
                
                // Enregistrement du mouvement de stock pour la vente
                StockMouvement mouvement = new StockMouvement();
                mouvement.setLot(lot);
                mouvement.setType(TypeMouvement.VENTE);
                mouvement.setQuantite(-quantitePrelevee);
                mouvement.setDate(LocalDateTime.now());
                mouvement.setMotif("Vente");
                stockMouvementRepository.save(mouvement);
                
                quantiteRestante -= quantitePrelevee;
            }
        }
        
        // Vérifier si toute la quantité a pu être prélevée
        if (quantiteRestante > 0) {
            throw new RuntimeException("Stock insuffisant pour le produit " + ligne.getProduit().getDesignation());
        }
    }

    /**
     * Récupère les ventes récentes (derniers 30 jours)
     * @return Liste des ventes récentes
     */
    public List<Vente> getVentesRecentes() {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(30);
        return venteRepository.findByDateBetween(dateDebut, LocalDateTime.now());
    }

    /**
     * Récupère les ventes d'un produit spécifique
     * @param produitId L'identifiant du produit
     * @return Liste des ventes contenant ce produit
     */
    public List<Vente> getVentesByProduitId(Long produitId) {
        return venteRepository.findByProduitId(produitId);
    }
}