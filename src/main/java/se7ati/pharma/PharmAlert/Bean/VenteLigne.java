package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenteLigne {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer quantite;
    private BigDecimal prixUnitaireTTC;
    
    @ManyToOne
    @JoinColumn(name = "vente_id")
    private Vente vente;
    
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
}
