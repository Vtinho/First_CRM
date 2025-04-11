package com.vitorM.CRM.service;

import com.vitorM.CRM.dto.OportunidadeDTO;
import com.vitorM.CRM.exception.RecursoNaoEncontradoException;
import com.vitorM.CRM.model.Cliente;
import com.vitorM.CRM.model.Oportunidade;
import com.vitorM.CRM.model.StatusOportunidade;
import com.vitorM.CRM.model.Usuario;
import com.vitorM.CRM.repository.ClienteRepository;
import com.vitorM.CRM.repository.OportunidadeRepository;
import com.vitorM.CRM.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OportunidadeService {

    private final OportunidadeRepository oportunidadeRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<OportunidadeDTO> listarTodas() {
        return oportunidadeRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OportunidadeDTO> listarPaginado(Pageable pageable) {
        return oportunidadeRepository.findAll(pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public OportunidadeDTO buscarPorId(Long id) {
        Oportunidade oportunidade = oportunidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade não encontrada com ID: " + id));
        return converterParaDTO(oportunidade);
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDTO> buscarPorCliente(Long clienteId) {
        return oportunidadeRepository.findByClienteId(clienteId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDTO> buscarPorStatus(StatusOportunidade status) {
        return oportunidadeRepository.findByStatus(status).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDTO> buscarPorPeriodoFechamento(LocalDate inicio, LocalDate fim, BigDecimal valorMinimo) {
        return oportunidadeRepository.findByDataFechamentoPrevistaBetween(inicio, fim).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalPorStatus(StatusOportunidade status) {
        BigDecimal total = oportunidadeRepository.calcularValorTotalPorStatus(status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public OportunidadeDTO salvar(OportunidadeDTO oportunidadeDTO) {
        Oportunidade oportunidade = converterParaEntidade(oportunidadeDTO);
        oportunidade.setDataAbertura(LocalDateTime.now());
        oportunidade = oportunidadeRepository.save(oportunidade);
        return converterParaDTO(oportunidade);
    }

    @Transactional
    public OportunidadeDTO atualizar(Long id, OportunidadeDTO oportunidadeDTO) {
        Oportunidade oportunidadeExistente = oportunidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade não encontrada com ID: " + id));

        Cliente cliente = clienteRepository.findById(oportunidadeDTO.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + oportunidadeDTO.getClienteId()));

        Usuario usuario = null;
        if (oportunidadeDTO.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(oportunidadeDTO.getUsuarioId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + oportunidadeDTO.getUsuarioId()));
        }

        oportunidadeExistente.setTitulo(oportunidadeDTO.getTitulo());
        oportunidadeExistente.setDescricao(oportunidadeDTO.getDescricao());
        oportunidadeExistente.setValor(oportunidadeDTO.getValor());
        oportunidadeExistente.setStatus(oportunidadeDTO.getStatus());
        oportunidadeExistente.setDataFechamentoPrevista(oportunidadeDTO.getDataFechamentoPrevista());

        if (StatusOportunidade.GANHA.equals(oportunidadeDTO.getStatus()) ||
                StatusOportunidade.PERDIDA.equals(oportunidadeDTO.getStatus())) {
            oportunidadeExistente.setDataFechamentoReal(LocalDate.now());
        }

        oportunidadeExistente.setCliente(cliente);
        oportunidadeExistente.setUsuario(usuario);

        oportunidadeExistente = oportunidadeRepository.save(oportunidadeExistente);
        return converterParaDTO(oportunidadeExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!oportunidadeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Oportunidade não encontrada com ID: " + id);
        }
        oportunidadeRepository.deleteById(id);
    }

    private OportunidadeDTO converterParaDTO(Oportunidade oportunidade) {
        OportunidadeDTO dto = new OportunidadeDTO();
        dto.setId(oportunidade.getId());
        dto.setTitulo(oportunidade.getTitulo());
        dto.setDescricao(oportunidade.getDescricao());
        dto.setValor(oportunidade.getValor());
        dto.setStatus(oportunidade.getStatus());
        dto.setDataFechamentoPrevista(oportunidade.getDataFechamentoPrevista());
        dto.setDataFechamentoReal(oportunidade.getDataFechamentoReal());

        if (oportunidade.getCliente() != null) {
            dto.setClienteId(oportunidade.getCliente().getId());
            dto.setNomeCliente(oportunidade.getCliente().getNome());
        }

        if (oportunidade.getUsuario() != null) {
            dto.setUsuarioId(oportunidade.getUsuario().getId());
            dto.setNomeUsuario(oportunidade.getUsuario().getNome());
        }
        return dto;
    }

    private Oportunidade converterParaEntidade(OportunidadeDTO dto) {
        Oportunidade oportunidade = new Oportunidade();
        oportunidade.setId(dto.getId());
        oportunidade.setTitulo(dto.getTitulo());
        oportunidade.setDescricao(dto.getDescricao());
        oportunidade.setValor(dto.getValor());
        oportunidade.setStatus(dto.getStatus());
        oportunidade.setDataFechamentoPrevista(dto.getDataFechamentoPrevista());
        oportunidade.setDataFechamentoReal(dto.getDataFechamentoReal());

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + dto.getClienteId()));
            oportunidade.setCliente(cliente);
        }

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + dto.getUsuarioId()));
            oportunidade.setUsuario(usuario);
        }
        return oportunidade;
    }

    @Transactional(readOnly = true)
    public Map<StatusOportunidade, Long> contarPorStatus() {
        List<Oportunidade> oportunidades = oportunidadeRepository.findAll();

        return oportunidades.stream()
                .collect(Collectors.groupingBy(Oportunidade::getStatus, Collectors.counting()));
    }
}
