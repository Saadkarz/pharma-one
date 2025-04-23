package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;
import se7ati.pharma.PharmAlert.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String identifiant;
    
    @Column(nullable = false)
    private String motDePasse;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private boolean actif;
    
    @OneToMany(mappedBy = "utilisateur")
    private List<StockMouvement> mouvements = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilisateur")
    private List<Vente> ventes = new ArrayList<>();
}
