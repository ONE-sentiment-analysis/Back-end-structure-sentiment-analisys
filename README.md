# Sentiment Analisys API (Java + Spring)

### Descrição
Projeto desenvolvido como estrutura base para o Hackaton da ORACLE. Esta aplicação, construída com Spring Boot, tem como objetivo integrar-se a um modelo de classificação de sentimentos fornecido por uma API externa desenvolvida em Python.

O sistema envia textos para o modelo de Machine Learning, recebe a análise de sentimento (como positivo, negativo ou neutro) e retorna o resultado estruturado para o cliente. Essa arquitetura permite que o backend Java funcione como intermediário entre o usuário e o modelo de IA, garantindo organização, segurança e escalabilidade.

###  Tecnologias utilizadas

- **Java 17+**
- **Spring Boot 2.5+**
- Spring Web
- DevTools
- Lombok
- HttpClient (Java 11+)
- Jackson (ObjectMapper)

### Docker
``
Docker build -t sentiment-spring-api .
Docker run -p 8080:8080 sentiment-spring-api
``

## ✅ O que já foi feito

- Criação dos DTOs:
  - `SentimentRequestModel`
  - `SentimentResponse`
- Controller com rota para análise de sentimento
- Serviço responsável pela integração com a API externa em Python
- Conversão JSON ↔ Objetos Java utilizando `ObjectMapper`
- Exception personalizada (`ExternalApiException`) para erros de comunicação

---
## 🚧 O que ainda falta implementar

- Persistência dos resultados em banco de dados (JPA + PostgreSQL/MySQL)
- Testes unitários (JUnit + Mockito)
- Criação de imagem Docker da aplicação
- Documentação da API com Swagger/OpenAPI
- Melhorar o DTO de resposta conforme o modelo Python evoluir
- Implementar `@ControllerAdvice` para padronizar respostas de erro

---

## 📡 Exemplo de requisição

### **POST /sentiment**

#### Body:
```json
{
  "text": "Esse produto é excelente!"
}


{
    "previsao": "positivo",
    "probabilidade": 0.92
}