package org.fiap.gs_final.security;

import org.fiap.gs_final.model.Usuario;
import org.fiap.gs_final.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Descobre o cadastro local de quem fez a requisição. O token local traz o
 * e-mail no "sub"; o token do Firebase traz o UID.
 */
@Component
public class UsuarioAtual {
    private final UsuarioRepository usuarios;

    public UsuarioAtual(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    public Usuario de(Jwt jwt) {
        if (jwt == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária");
        String issuer = jwt.getClaimAsString(JwtClaimNames.ISS);
        var usuario = SecurityConfig.LOCAL_ISSUER.equals(issuer)
                ? usuarios.findByEmailIgnoreCase(jwt.getSubject())
                : usuarios.findByFirebaseUid(jwt.getSubject());
        return usuario.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Conta ainda não vinculada; chame POST /api/auth/firebase"));
    }

    public static boolean ehGestor(Usuario usuario) {
        return usuario != null && "GESTOR".equals(usuario.getPapel());
    }
}
