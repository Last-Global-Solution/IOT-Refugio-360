package org.fiap.gs_final.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.fiap.gs_final.ai.OrientacaoService;
import org.fiap.gs_final.dto.OrientacaoResponseDTO;
import org.fiap.gs_final.security.UsuarioAtual;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class OrientacaoController {
    private final OrientacaoService service;
    private final UsuarioAtual usuarioAtual;

    public OrientacaoController(OrientacaoService service, UsuarioAtual usuarioAtual) {
        this.service = service;
        this.usuarioAtual = usuarioAtual;
    }

    /** Gera uma nova orientação na hora, por exemplo com outro perfil de público. */
    @PostMapping("/ocorrencias/{id}/orientacao")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CIDADAO','GESTOR')")
    public OrientacaoResponseDTO orientar(@PathVariable Long id,
                                          @RequestParam(required = false) String perfil,
                                          @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return service.orientarPara(id, perfil, usuarioAtual.de(jwt));
    }
}
