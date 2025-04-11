package com.vitorM.CRM.controler;

import com.vitorM.CRM.dto.ContatoDTO;
import com.vitorM.CRM.service.ContatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contatos")
@RequiredArgsConstructor
public class ContatoController {

    private final ContatoService contatoService;

    @GetMapping
    public ResponseEntity<List<ContatoDTO>> listarTodos() {
        return ResponseEntity.ok(contatoService.listarTodos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ContatoDTO>> listarPaginado(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(contatoService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContatoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contatoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ContatoDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(contatoService.buscarPorCliente(clienteId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ContatoDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(contatoService.(email));
    }

    @PostMapping
    public ResponseEntity<ContatoDTO> salvar(@RequestBody @Valid ContatoDTO contatoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contatoService.salvar(contatoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContatoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ContatoDTO contatoDTO) {
        return ResponseEntity.ok(contatoService.atualizar(id, contatoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        contatoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}