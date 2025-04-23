package se7ati.pharma.PharmAlert.Bean;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fournisseur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    
    @OneToMany(mappedBy = "fournisseur")
    private List<Produit> produits = new ArrayList<>();
}
