package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.DTO.request.PessoaRequest;
import br.com.one.sentiment_analysis.DTO.response.PessoaCadastroResponse;
import br.com.one.sentiment_analysis.DTO.response.PessoaResponse;
import br.com.one.sentiment_analysis.exception.ResourceNotFoundException;
import br.com.one.sentiment_analysis.model.user.User;
import br.com.one.sentiment_analysis.repository.UserRepository;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Endpoint para gestão de pessoas", description = "Gerencia o cadastro e visualização de usuários")
public class AuthController {

    @Autowired
    private UserRepository repository;

    @PostMapping
    @Operation(
        summary = "Cadastrar nova pessoa",
        description = "Recebe dados de cadastro (nome) e cria um novo usuário")
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

        User novaPessoa = new User(request.nome(), request.email(), request.senha());

        User pessoaSalva = repository.save(novaPessoa);

        PessoaCadastroResponse response = new PessoaCadastroResponse(
                pessoaSalva.getId(),
                pessoaSalva.getNome()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar pessoas",
        description = "Retorna uma lista paginada de usuários cadastrados")
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
//     TODO: listar usuários apenas se a ROLE for ADMIN
    public ResponseEntity<Page<PessoaResponse>> listarPessoas(
            @PageableDefault(size = 15, sort = "nome") Pageable pageable) {

        Page<User> paginaPessoas = repository.findAll(pageable);

        Page<PessoaResponse> response = paginaPessoas.map(pessoa ->
                new PessoaResponse(
                        pessoa.getId(),
                        pessoa.getNome(),
                        pessoa.getAvaliacoes().size()
                )
        );

        return ResponseEntity.ok(response);
    }

//     TODO: usuário só pode pegar detalhes da própria conta
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
    public ResponseEntity<PessoaResponse> buscarPorId(@PathVariable Long id) {

        User pessoa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));

        PessoaResponse response = new PessoaResponse(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getAvaliacoes().size()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleta usuário",
        description = "Remove usuário pelo Id, se usuário não existe retorna not found"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Usuário deletado com sucesso"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") long userId){
        Optional<User> existUser = repository.findById(userId);
        if (existUser.isPresent()) {
            repository.deleteById(userId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
