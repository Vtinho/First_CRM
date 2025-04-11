package com.vitorM.CRM.controler;

import com.vitorM.CRM.dto.OportunidadeDTO;
import com.vitorM.CRM.model.StatusOportunidade;
import com.vitorM.CRM.service.OportunidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oportunidades")
@RequiredArgsConstructor
public class OportunidadeController {

    private final OportunidadeService oportunidadeService;

    @GetMapping
    public ResponseEntity<List<OportunidadeDTO>> listarTodas() {
        return ResponseEntity.ok(oportunidadeService.listarTodas());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<OportunidadeDTO>> listarPaginado(
            @PageableDefault(size = 10, sort = "dataAbertura") Pageable pageable) {
        return ResponseEntity.ok(oportunidadeService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadeService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<OportunidadeDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(oportunidadeService.buscarPorCliente(clienteId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OportunidadeDTO>> buscarPorStatus(@PathVariable StatusOportunidade status) {
        return ResponseEntity.ok(oportunidadeService.buscarPorStatus(status));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<OportunidadeDTO>> buscarPorPeriodoFechamentoPrevista(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) BigDecimal valorMinimo) {

        return ResponseEntity.ok(oportunidadeService.buscarPorPeriodoFechamento(inicio, fim, valorMinimo));
    }

    @PostMapping
    public ResponseEntity<OportunidadeDTO> salvar(@RequestBody @Valid OportunidadeDTO oportunidadeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(oportunidadeService.salvar(oportunidadeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OportunidadeDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid OportunidadeDTO oportunidadeDTO) {
        return ResponseEntity.ok(oportunidadeService.atualizar(id, oportunidadeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        oportunidadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio/valor-total")
    public ResponseEntity<BigDecimal> calcularValorTotalPorStatus(
            @RequestParam(required = false) StatusOportunidade status) {
        return ResponseEntity.ok(oportunidadeService.calcularValorTotalPorStatus(status));
    }

    @GetMapping("/relatorio/contagem-por-status")
    public ResponseEntity<Map<StatusOportunidade, Long>> contarPorStatus() {
        // Este método precisa ser implementado no service
        return ResponseEntity.ok(oportunidadeService.contarPorStatus());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OportunidadeDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusOportunidade novoStatus) {

        OportunidadeDTO oportunidadeDTO = oportunidadeService.buscarPorId(id);
        oportunidadeDTO.setStatus(novoStatus);
        return ResponseEntity.ok(oportunidadeService.atualizar(id, oportunidadeDTO));
    }
}


