package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;
import se7ati.pharma.PharmAlert.enums.TypeAlerte;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private TypeAlerte type;
    
    private String message;
    private boolean traitee;
    private LocalDateTime dateCreation;
    
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot; // Optionnel
}
