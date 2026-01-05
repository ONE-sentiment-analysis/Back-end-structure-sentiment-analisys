package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.PessoaRequest;
import br.com.one.sentiment_analysis.dto.response.PessoaCadastroResponse;
import br.com.one.sentiment_analysis.dto.response.PessoaResponse;
import br.com.one.sentiment_analysis.exception.ResourceNotFoundException;
import br.com.one.sentiment_analysis.model.pessoa.Pessoa;
import br.com.one.sentiment_analysis.repository.PessoaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pessoas")
@Tag(name = "Endpoint para gestão de pessoas", description = "Gerencia o cadastro e visualização de usuários")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @PostMapping
    @Operation(summary = "Cadastrar nova pessoa", description = "Recebe dados de cadastro (nome) e cria um novo usuário")
    @ApiResponse(
            responseCode = "201",
            description = "Pessoa cadastrada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"id\": 1, \"nome\": \"Nome da Pessoa\"}"
                    )
            )
    )
    public ResponseEntity<PessoaCadastroResponse> cadastrarPessoa(@RequestBody @Valid PessoaRequest request) {
        Pessoa novaPessoa = new Pessoa(request.nome());

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        PessoaCadastroResponse response = new PessoaCadastroResponse(
                pessoaSalva.getId(),
                pessoaSalva.getNome()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pessoas", description = "Retorna uma lista paginada de usuários cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"content\": [ { \"id\": 1, \"nome\": \"Nome da Pessoa\", \"pageable\": \"INSTANCE\", \"totalPages\": 1, \"totalElements\": 1 }"
                    )
            )
    )
    public ResponseEntity<Page<PessoaResponse>> listarPessoas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<Pessoa> paginaPessoas = pessoaRepository.findAll(pageable);

        Page<PessoaResponse> response = paginaPessoas.map(pessoa ->
                new PessoaResponse(
                        pessoa.getId(),
                        pessoa.getNome(),
                        pessoa.getAvaliacoes().size()
                )
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pessoa por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"id\": 1, \"nome\": \"Nome da Pessoa\"}"
                    )
            )
    )
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public ResponseEntity<PessoaResponse> buscarPorId(@PathVariable Long id) {

        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));

        PessoaResponse response = new PessoaResponse(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getAvaliacoes().size()
        );

        return ResponseEntity.ok(response);
    }
}
