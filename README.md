# Sentiment Analisys API (Java + Spring)

## Descrição
Projeto desenvolvido como estrutura base para o Hackaton da ORACLE. Esta aplicação, construída com Spring Boot, tem como objetivo integrar-se a um modelo de classificação de sentimentos fornecido por uma API externa desenvolvida em Python.

O sistema envia textos para o modelo de Machine Learning, recebe a análise de sentimento (como positivo, negativo ou neutro) e retorna o resultado estruturado para o cliente. Essa arquitetura permite que o backend Java funcione como intermediário entre o usuário e o modelo de IA, garantindo organização, segurança e escalabilidade.

[![Version](https://img.shields.io/badge/version-1.0.0-green.svg)]()
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

## Quick Start

Get up and running in seconds:

```javascript
docker compose -up --build
```



##  Tecnologias utilizadas

- **Java 17+**
- **Spring Boot 2.5+**
- Spring Web
- DevTools
- Lombok
- HttpClient (Java 11+)
- Jackson (ObjectMapper)
- JUnit + Mockito + H2
- Resilience4j (Circuit Breaker, Retry, Rate Limiter, Bulkhead, TimeLimiter)
- Observabilidade: Actuator + Prometheus + Grafana
- Dockerfile e docker-compose

## Features

- 🚀 **Performance** - Lightning fast performance
- 🎯 **Easy to Use** - Simple and intuitive API
- 💎 **Lightweight** - Minimal dependencies



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
  - `Dockerfile e docker compose`

---
## Configuration

### Como rodar Prometheus
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
### Main Structure

```
src/
├── main/
│   ├── java/
│   │   ├── cache/         # Implementações de cache (se houver)
│   │   ├── config/        # Configurações da aplicação (Spring, beans, segurança, etc.)
│   │   ├── controller/    # Controllers REST (endpoints da API)
│   │   ├── dto/           # Data Transfer Objects (entrada/saída da API)
│   │   ├── exception/     # Classes de exceção personalizadas
│   │   ├── handler/       # Exception handlers globais (ex: @ControllerAdvice)
│   │   ├── model/         # Entidades e modelos de domínio
│   │   ├── repository/    # Interfaces de persistência (Spring Data JPA)
│   │   ├── service/       # Regras de negócio e integração com API externa
│   │   └── application/   # Classe principal (Spring Boot Application)
│   │
│   └── resources/         # Arquivos de configuração (application.properties, application.yml)
│
└── test/                  # Testes unitários e de integração
    ├── java/              # Código de testes
    └── resources/         # Configurações específicas para testes


```

## 📡 Endpoints

| Método | Rota                     | Descrição                                                                 |
|--------|--------------------------|---------------------------------------------------------------------------|
| POST   | `/api/v1/sentiment`             | Recebe um texto e retorna a análise de sentimento (positivo, negativo, neutro). |                              |
| GET    | `/swagger-ui/index.html` | Interface interativa da documentação da API.                              |
| POST   | `/api/v1/pessoas`     | Cadastra uma nova pessoa (recebe dados de cadastro, como nome).           |
| GET    | `/api/v1/pessoas`     | Lista todas as pessoas cadastradas (paginado, ordenado por nome).         |
| GET    | `/api/v1/pessoas/{id}`| Busca os detalhes de uma pessoa específica pelo ID.                       |

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
```

## Security

### Security Measures

- 🔒 **Encryption:** All data is encrypted in transit and at rest
- 🛡️ **Authentication:** Secure API key authentication
- 🔍 **Validation:** Input validation and sanitization
- 📊 **Monitoring:** Continuous security monitoring

### Reporting Security Issues

If you discover a security vulnerability, please:

1. **Do NOT** create a public issue
2. Email us at security@example.com
3. Include detailed information about the vulnerability
4. Allow time for us to address the issue before disclosure

### Security Best Practices

- Keep your API keys secure
- Use HTTPS in production
- Regularly update dependencies
- Follow the principle of least privilege


## Roadmap

### Current Version (v1.0)
- ✅ Core functionality
- ✅ Basic API
- ✅ Documentation

### Upcoming (v1.1)
- 🔄 Performance improvements
- 🔄 New features
- 🔄 Bug fixes

### Future (v2.0)
- 📋 Complete rewrite
- 📋 Breaking changes
- 📋 New architecture

### Ideas
- 💡 OCI autonomus Database
- 💡 OCI deploy
- 💡 Kubernetes

## Team

### Back-end Core Team

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/Cauan77.png" width="100px" alt=""/><br />
      <b>Cauan Henrique</b><br />
      <i>Engenheiro de Software</i><br />
      <a href="https://github.com/Cauan77">GitHub</a>
    </td>
    <td align="center">
      <img src="https://github.com/stevopablo.png" width="100px" alt=""/><br />
      <b>Estevão Pablo</b><br />
      <i>Engenheiro de Software</i><br />
      <a href="https://github.com/stevopablo">GitHub</a>
    </td>
  </tr>
</table>