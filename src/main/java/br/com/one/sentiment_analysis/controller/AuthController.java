package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.UserRegisterRequest;
import br.com.one.sentiment_analysis.dto.request.UserLoginRequest;
import br.com.one.sentiment_analysis.dto.response.PessoaCadastroResponse;
import br.com.one.sentiment_analysis.dto.response.PessoaResponse;
import br.com.one.sentiment_analysis.dto.response.UserLoginResponse;
import br.com.one.sentiment_analysis.config.JwtUtil;
import br.com.one.sentiment_analysis.exception.InvalidPasswordException;
import br.com.one.sentiment_analysis.exception.ResourceNotFoundException;
import br.com.one.sentiment_analysis.exception.UserAlreadyExistException;
import br.com.one.sentiment_analysis.exception.UserNotFoundException;
import br.com.one.sentiment_analysis.model.user.User;
import br.com.one.sentiment_analysis.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Endpoint para gestão de pessoas", description = "Gerencia o cadastro e visualização de usuários")
public class AuthController {

    @Autowired
    private UserRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    @Operation(
        summary = "Cadastrar nova pessoa",
        description = "Recebe dados de cadastro (nome, email, senha) e cria um novo usuário")
    @ApiResponse(
            responseCode = "201",
            description = "Pessoa cadastrada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"id\": 1," +
                                    " \"nome\": \"Nome da Pessoa\"" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<PessoaCadastroResponse> cadastrarPessoa(@RequestBody @Valid UserRegisterRequest request) {
        log.info("Tentativa de cadastro para email: {}", request.email());
        if (repository.findByEmail(request.email()).isPresent()){
            log.warn("Cadastro negado: email já existente {}", request.email());
            throw new UserAlreadyExistException("Email já cadastrado: "+ request.email());
        }
        User novaPessoa = new User(request.name(), request.email(), encoder.encode(request.password()));

        User pessoaSalva = repository.save(novaPessoa);

        PessoaCadastroResponse response = new PessoaCadastroResponse(
                pessoaSalva.getId(),
                pessoaSalva.getNome()
        );
        log.info("Usuário {} cadastrado com sucesso em {}", pessoaSalva.getEmail(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Realizar Login",
            description = "Autentica usuário e retorna um token JWT"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \" email\": user@gmail.com, "+
                                    "\"token\": eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWJs..."
                    )
            )
    )
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        log.info("Tentativa de login para email: {}", request.email());
        User userExist = repository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (!encoder.matches(request.password(), userExist.getSenha())) {
            log.warn("Senha incorreta para usuário {}", request.email());
            throw new InvalidPasswordException("Senha incorreta");
        }

        String token = JwtUtil.generateToken(userExist.getEmail());
        log.info("Login realizado com sucesso para {} em {}", userExist.getEmail(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserLoginResponse(
                userExist.getEmail(),
                token
        ));
    }
//    TODO: Deve retorna apenas com Role ADMIN
    @GetMapping("/users")
    @Operation(
        summary = "Listar pessoas",
        description = "Retorna uma lista paginada de usuários cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"content\": [ { \"id\": 1," +
                                    " \"nome\": \"Nome da Pessoa\"," +
                                    " \"pageable\": \"INSTANCE\"," +
                                    " \"totalPages\": 1," +
                                    " \"totalElements\": 1 }"
                    )
            )
    )
    public ResponseEntity<Page<PessoaResponse>> listarPessoas(
            @PageableDefault(size = 15, sort = "nome") Pageable pageable) {
        log.debug("Listando usuários com paginação: {}", pageable);
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

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pessoa por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"id\": 1," +
                                    " \"nome\": \"Nome da Pessoa\"" +
                                    "}"
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @RequestBody UserRegisterRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if(request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.name() != null) {
            user.setNome(request.name());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setSenha(request.password());
        }
        User updatedUser = repository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", updatedUser.getId());
        response.put("nome", updatedUser.getNome());
        response.put("email", updatedUser.getEmail());
        response.put("avaliacoes", updatedUser.getAvaliacoes().size());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta usuário",
        description = "Remove usuário pelo Id, se usuário não existe retorna not found"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Usuário deletado com sucesso"
    )
    public ResponseEntity<String> deleteUserById(@PathVariable("id") long userId){
        log.info("Tentativa de exclusão do usuário ID {}", userId);
        Optional<User> existUser = repository.findById(userId);
        if (existUser.isPresent()) {
            repository.deleteById(userId);
            log.info("Usuário ID {} deletado com sucesso", userId);
            return ResponseEntity.noContent().build();
        }
        log.warn("Usuário ID {} não encontrado para exclusão", userId);
        return ResponseEntity.notFound().build();
    }

}
