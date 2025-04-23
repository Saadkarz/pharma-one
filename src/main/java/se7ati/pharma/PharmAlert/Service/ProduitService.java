package se7ati.pharma.PharmAlert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se7ati.pharma.PharmAlert.Bean.Produit;
import se7ati.pharma.PharmAlert.repositories.ProduitRepository;

import java.util.List;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;


    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }


    public Produit getProduitById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id " + id));
    }

    /**
     * Crée un nouveau produit
     * @param produit Produit à créer
     * @return Le produit créé
     */
    public Produit createProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    /**
     * Met à jour un produit existant
     * @param id Identifiant du produit à mettre à jour
     * @param produitDetails Nouvelles informations du produit
     * @return Le produit mis à jour
     * @throws RuntimeException si le produit n'existe pas
     */
    public Produit updateProduit(Long id, Produit produitDetails) {
        Produit produit = getProduitById(id);
        
        // Mise à jour des propriétés du produit
        produit.setDesignation(produitDetails.getDesignation());
        produit.setCodeEAN(produitDetails.getCodeEAN());
        produit.setForme(produitDetails.getForme());
        produit.setDosage(produitDetails.getDosage());
        produit.setPrixAchatHT(produitDetails.getPrixAchatHT());
        produit.setPrixVenteTTC(produitDetails.getPrixVenteTTC());
        produit.setTauxTVA(produitDetails.getTauxTVA());
        produit.setCategorie(produitDetails.getCategorie());
        produit.setSeuilStockMin(produitDetails.getSeuilStockMin());
        produit.setDelaiAlerteExpiration(produitDetails.getDelaiAlerteExpiration());
        produit.setEmplacement(produitDetails.getEmplacement());
        produit.setActif(produitDetails.isActif());
        produit.setFournisseur(produitDetails.getFournisseur());
        
        return produitRepository.save(produit);
    }

    /**
     * Supprime un produit
     * @param id Identifiant du produit à supprimer
     * @throws RuntimeException si le produit n'existe pas
     */
    public void deleteProduit(Long id) {
        Produit produit = getProduitById(id);
        produitRepository.delete(produit);
    }
}