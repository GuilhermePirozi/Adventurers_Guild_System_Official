# 🧙‍♂️ Adventurers Guild System

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-API-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-Search-yellow)
![Docker](https://img.shields.io/badge/Docker-Container-blue)

---

## 📌 Sobre o projeto

API REST para gerenciamento de uma guilda de aventureiros, com suporte a:

- Persistência em PostgreSQL  
- Cache com Redis  
- Buscas avançadas e agregações com Elasticsearch  

O projeto simula um ambiente real de backend, com foco em boas práticas, organização e escalabilidade.

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

- **Controller** → entrada da API (REST)  
- **Service** → regras de negócio  
- **Repository** → acesso a dados (JPA)  
- **DTO** → transporte de dados entre camadas  

### 🔗 Integrações

- PostgreSQL → persistência de dados  
- Redis → cache  
- Elasticsearch → busca e agregações  

---

## ⭐ Diferenciais

- Cache com Redis para otimização de performance  
- Elasticsearch com:
  - busca fuzzy  
  - busca por frase  
  - busca multicampos  
  - agregações  
- Uso de DTOs para desacoplamento  
- Queries otimizadas com JPA  
- Ambiente completo com Docker  
- Estrutura próxima de ambiente real  

---

## 🚀 Tecnologias utilizadas

- Java 21  
- Spring Boot  
- Spring Data JPA  
- Spring Data Elasticsearch  
- PostgreSQL  
- Redis  
- Elasticsearch  
- Kibana  
- Docker / Docker Compose  
- Postman  

---

## 📦 Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

- Java 21+  
- Maven  
- Docker + Docker Compose  
- Postman (opcional)  

---

## ⚙️ Configuração do projeto

### 🔹 Datasource da aplicação

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=guildpass

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.elasticsearch.uris=http://localhost:9200
spring.cache.type=redis
