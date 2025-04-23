package se7ati.pharma.PharmAlert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import se7ati.pharma.PharmAlert.Bean.Utilisateur;
import se7ati.pharma.PharmAlert.repositories.UtilisateurRepository;
import se7ati.pharma.PharmAlert.security.JwtTokenUtil;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Crée un nouvel utilisateur avec mot de passe hashé
     * @param utilisateur Les données de l'utilisateur à créer
     * @return L'utilisateur créé
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        // Vérifier si l'identifiant existe déjà
        if (utilisateurRepository.findByIdentifiant(utilisateur.getIdentifiant()).isPresent()) {
            throw new RuntimeException("Cet identifiant existe déjà");
        }
        
        // Hasher le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        
        // Activer l'utilisateur par défaut
        if (utilisateur.isActif()) {
            utilisateur.setActif(true);
        }
        
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Authentifie un utilisateur et génère un token JWT
     * @param identifiant L'identifiant de l'utilisateur
     * @param motDePasse Le mot de passe de l'utilisateur
     * @return Le token JWT si l'authentification réussit
     */
    public String authentifier(String identifiant, String motDePasse) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByIdentifiant(identifiant);
        
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Identifiant ou mot de passe incorrect");
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        
        // Vérifier si l'utilisateur est actif
        if (!utilisateur.isActif()) {
            throw new RuntimeException("Ce compte est désactivé");
        }
        
        // Vérifier le mot de passe
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Identifiant ou mot de passe incorrect");
        }
        
        // Générer un token JWT
        return jwtTokenUtil.generateToken(utilisateur);
    }

    /**
     * Récupère tous les utilisateurs
     * @return Liste de tous les utilisateurs
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur correspondant
     */
    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id " + id));
    }

    /**
     * Met à jour un utilisateur existant
     * @param id L'identifiant de l'utilisateur à mettre à jour
     * @param utilisateurDetails Les nouvelles données de l'utilisateur
     * @return L'utilisateur mis à jour
     */
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur utilisateur = getUtilisateurById(id);
        
        // Mise à jour des informations
        if (utilisateurDetails.getIdentifiant() != null) {
            // Vérifier si le nouvel identifiant existe déjà
            if (!utilisateur.getIdentifiant().equals(utilisateurDetails.getIdentifiant()) && 
                    utilisateurRepository.findByIdentifiant(utilisateurDetails.getIdentifiant()).isPresent()) {
                throw new RuntimeException("Cet identifiant existe déjà");
            }
            utilisateur.setIdentifiant(utilisateurDetails.getIdentifiant());
        }
        
        // Mise à jour du mot de passe si fourni
        if (utilisateurDetails.getMotDePasse() != null && !utilisateurDetails.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDetails.getMotDePasse()));
        }
        
        // Mise à jour du rôle si fourni
        if (utilisateurDetails.getRole() != null) {
            utilisateur.setRole(utilisateurDetails.getRole());
        }
        
        // Mise à jour du statut actif
        utilisateur.setActif(utilisateurDetails.isActif());
        
        return utilisateurRepository.save(utilisateur);
    }
}