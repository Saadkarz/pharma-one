package se7ati.pharma.PharmAlert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se7ati.pharma.PharmAlert.Bean.Alerte;
import se7ati.pharma.PharmAlert.Bean.Lot;
import se7ati.pharma.PharmAlert.Bean.Produit;
import se7ati.pharma.PharmAlert.enums.TypeAlerte;
import se7ati.pharma.PharmAlert.repositories.AlerteRepository;
import se7ati.pharma.PharmAlert.repositories.LotRepository;
import se7ati.pharma.PharmAlert.repositories.ProduitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlerteService {

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private ProduitRepository produitRepository;

    /**
     * Récupère toutes les alertes actives (non traitées)
     * @return Liste des alertes non traitées
     */
    public List<Alerte> getAlertesActives() {
        return alerteRepository.findByTraiteeFalse();
    }

    /**
     * Récupère toutes les alertes d'un type spécifique
     * @param type Le type d'alerte
     * @return Liste des alertes du type spécifié
     */
    public List<Alerte> getAlertesByType(TypeAlerte type) {
        return alerteRepository.findByType(type);
    }

    /**
     * Récupère toutes les alertes
     * @return Liste de toutes les alertes
     */
    public List<Alerte> getAllAlertes() {
        return alerteRepository.findAll();
    }

    /**
     * Marque une alerte comme traitée
     * @param id L'identifiant de l'alerte
     * @return L'alerte mise à jour
     */
    @Transactional
    public Alerte traiterAlerte(Long id) {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée avec l'id " + id));
        
        alerte.setTraitee(true);
        return alerteRepository.save(alerte);
    }

    /**
     * Génère les alertes de stock bas et d'expiration proche
     * Méthode exécutée automatiquement tous les jours à minuit
     */
    @Scheduled(cron = "0 0 0 * * ?") // Tous les jours à minuit
    @Transactional
    public void genererAlertes() {
        genererAlertesStockBas();
        genererAlertesExpirationProche();
    }

    /**
     * Génère les alertes pour les produits dont le stock est inférieur au seuil minimum
     */
    private void genererAlertesStockBas() {
        List<Produit> produitsStockBas = produitRepository.findProduitsWithLowStock();
        
        for (Produit produit : produitsStockBas) {
            // Vérifier si une alerte de stock bas active existe déjà pour ce produit
            List<Alerte> alertesExistantes = alerteRepository.findByProduitId(produit.getId());
            boolean alerteExiste = alertesExistantes.stream()
                    .anyMatch(a -> a.getType() == TypeAlerte.STOCK_BAS && !a.isTraitee());
            
            if (!alerteExiste) {
                Alerte alerte = new Alerte();
                alerte.setType(TypeAlerte.STOCK_BAS);
                alerte.setProduit(produit);
                alerte.setMessage("Stock bas pour le produit " + produit.getDesignation());
                alerte.setTraitee(false);
                alerte.setDateCreation(LocalDateTime.now());
                
                alerteRepository.save(alerte);
            }
        }
    }

    /**
     * Génère les alertes pour les lots dont la date d'expiration est proche
     */
    private void genererAlertesExpirationProche() {
        List<Produit> produits = produitRepository.findByActifTrue();
        LocalDate aujourdhui = LocalDate.now();
        
        for (Produit produit : produits) {
            if (produit.getDelaiAlerteExpiration() != null) {
                LocalDate dateAlerte = aujourdhui.plusDays(produit.getDelaiAlerteExpiration());
                
                // Rechercher les lots qui expirent bientôt pour ce produit
                List<Lot> lotsConcernes = lotRepository.findLotsExpiringBetween(aujourdhui, dateAlerte);
                
                for (Lot lot : lotsConcernes) {
                    if (lot.getProduit().getId().equals(produit.getId()) && lot.getQuantite() > 0) {
                        // Vérifier si une alerte d'expiration active existe déjà pour ce lot
                        List<Alerte> alertesExistantes = alerteRepository.findByLotId(lot.getId());
                        boolean alerteExiste = alertesExistantes.stream()
                                .anyMatch(a -> a.getType() == TypeAlerte.EXPIRATION_PROCHE && !a.isTraitee());
                        
                        if (!alerteExiste) {
                            Alerte alerte = new Alerte();
                            alerte.setType(TypeAlerte.EXPIRATION_PROCHE);
                            alerte.setProduit(produit);
                            alerte.setLot(lot);
                            alerte.setMessage("Lot " + lot.getNumeroLot() + " du produit " + 
                                    produit.getDesignation() + " expire le " + lot.getDateExpiration());
                            alerte.setTraitee(false);
                            alerte.setDateCreation(LocalDateTime.now());
                            
                            alerteRepository.save(alerte);
                        }
                    }
                }
            }
        }
    }

    /**
     * Force la génération des alertes (utile pour les tests ou l'interface admin)
     */
    @Transactional
    public void forceGenererAlertes() {
        genererAlertes();
    }
}