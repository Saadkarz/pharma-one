package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;
import se7ati.pharma.PharmAlert.enums.TypeMouvement;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMouvement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private TypeMouvement type;
    
    private Integer quantite;
    private LocalDateTime date;
    private String motif;
    
    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
