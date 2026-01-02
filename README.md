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


## ✅ O que já foi feito

- Criação dos DTOs:
  - `SentimentRequestModel`
  - `SentimentResponse`
- Controller com rota para análise de sentimento
- Serviço responsável pela integração com a API externa em Python
- Conversão JSON ↔ Objetos Java utilizando `ObjectMapper`
- Exception personalizada (`ExternalApiException`) para erros de comunicação
- Documentação com Swagger/OpenAPI
- Teste Unitários 
  - `JUnit + Mockito + H2 database`
- Spring Securoty Para proteger rotas
- Observabilidade
  - `Actuator`
  - `Prometheus` 
  - `Grafana`
  - `Actuator` 
- Resilience4j
  - `Circuit Breaker`
  - `Retry`
  - `Rate Limiter`
  - `Bulkhead`
  - `TimeLimiter`
- DockerFile

---

### Como rodar usando Docker
``
  Docker build -t sentiment-spring-api .
``

``
  Docker run -p 8080:8080 sentiment-spring-api
``


---

## Como rodar Prometheus
- [Instale](https://prometheus.io/download/) prometheus de acordo com OS
- Extraia a pasta e edite prometheus.yaml
````
 global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'springboot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
````

- Rode o comando no Terminal :  prometheus.exe
- config.file=prometheus.yml

- O Pormetheus estará rodando em: http://localhost:9090


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