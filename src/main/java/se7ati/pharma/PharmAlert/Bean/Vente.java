package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;
import se7ati.pharma.PharmAlert.enums.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime date;
    private BigDecimal totalTTC;
    
    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
    
    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL)
    private List<VenteLigne> lignes = new ArrayList<>();
}
