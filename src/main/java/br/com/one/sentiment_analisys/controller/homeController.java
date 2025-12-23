package br.com.one.sentiment_analisys.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Rotas de testes do Swagger", description = "Endpoints para verificar se está configurado e funcionando tudo corretamente")
public class homeController {

    @GetMapping
    @Operation(summary = "Retorna um Olá, Spring Boot")
    @ApiResponse(responseCode = "200", description = "GET funcionando")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<String> helloSpring() {
        return ResponseEntity.ok("Olá, Spring Boot");
    }



    @GetMapping("/hello/{name}")
    @Operation(summary = "Retorna uma Saudação para o Usuário", description = "Preencha campo Nome")
    @ApiResponse(
        responseCode = "200",
        description = "GET saudação funcionando",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{ \"mensagem\": \"Olá, Papai Noel\" }"))
    )
    public ResponseEntity<String> helloName(@PathVariable String name) {
        return ResponseEntity.ok("Olá, " + name.toUpperCase().trim());
    }
}
