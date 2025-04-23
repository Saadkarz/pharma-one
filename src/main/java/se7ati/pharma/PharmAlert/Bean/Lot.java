package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String numeroLot;
    private LocalDate dateExpiration;
    private Integer quantite;
    
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL)
    private List<StockMouvement> mouvements = new ArrayList<>();
    
    @OneToMany(mappedBy = "lot")
    private List<Alerte> alertes = new ArrayList<>();
}
