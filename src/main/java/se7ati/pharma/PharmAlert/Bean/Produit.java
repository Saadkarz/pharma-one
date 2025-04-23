package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String designation;
    private String codeEAN;
    private String forme;
    private String dosage;
    
    private BigDecimal prixAchatHT;
    private BigDecimal prixVenteTTC;
    private BigDecimal tauxTVA;
    
    private String categorie;
    private Integer seuilStockMin;
    private Integer delaiAlerteExpiration; // en jours
    private String emplacement;
    private boolean actif;
    
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;
    
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<Lot> lots = new ArrayList<>();
    
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<Alerte> alertes = new ArrayList<>();
    
    @OneToMany(mappedBy = "produit")
    private List<VenteLigne> venteLignes = new ArrayList<>();
}
