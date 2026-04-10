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

Aqui está o conteúdo convertido para Markdown:
markdown# 🗡️ AT System — Guia de Configuração e Execução

---

## ⚠️ Configuração inicial do Hibernate (IMPORTANTE)

### 🔹 Primeira execução
```properties
spring.jpa.hibernate.ddl-auto=update
```

### 🔹 Após a primeira execução
```properties
spring.jpa.hibernate.ddl-auto=validate
```

### ❗ Regra importante

- Use `update` **somente na primeira execução**
- Depois utilize **sempre** `validate`

### 💡 Motivo

- `update` → cria/ajusta estrutura automaticamente
- `validate` → apenas valida (padrão de mercado e mais seguro)

---

## 🐳 Subindo o ambiente com Docker

### 🔹 1. Elasticsearch, Kibana e Redis

```yaml
name: at-system
services:
  elasticsearch:
    image: leogloriainfnet/elastic-tp2-spring:1.0-windows
    container_name: elasticsearch-tp3
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
    networks:
      - elastic

  kibana:
    image: docker.elastic.co/kibana/kibana:9.2.5
    container_name: kibana-tp3
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
    networks:
      - elastic

  redis-tp3:
    image: redis:latest
    container_name: redis-cache-tp3
    ports:
      - "6379:6379"

networks:
  elastic:
    driver: bridge
```

#### ▶️ Subir serviços

```bash
docker-compose up -d
```

---

### 🔹 2. PostgreSQL (imagem do professor)

```bash
docker run -d --name postgres-tp2 \
  -e POSTGRES_PASSWORD=guildpass \
  -p 5432:5432 \
  leogloriainfnet/postgres-tp2-spring:2.0-mac
```

---

### 🔹 3. Ajustar senha

```bash
docker exec -it  psql -U postgres
ALTER USER postgres WITH PASSWORD 'guildpass';
```

---

### 🔹 4. Verificar containers

```bash
docker ps
```

**Containers esperados:**

- `postgres-tp2`
- `elasticsearch-tp3`
- `kibana-tp3`
- `redis-cache-tp3`

---

## ▶️ Rodando a aplicação

```bash
mvn spring-boot:run
```

🌐 Acesso: [http://localhost:8080](http://localhost:8080)

---

## 🗄️ Popular banco

Execute os scripts SQL na ordem:

1. `aventureiros`
2. `companheiros`
3. `missoes`
4. `participacoes_missao`

---

## ⚡ Teste rápido

```bash
curl http://localhost:8080/aventureiros
```

---

## 🔥 Endpoints principais

### 🧙 Aventureiros

```
GET  /aventureiros
GET  /aventureiros?size=100
GET  /aventureiros/busca?trecho=Aegwynn
GET  /aventureiros/{id}
GET  /aventureiros/{id}/perfil

PATCH /aventureiros/{id}
PATCH /aventureiros/{id}/encerrar-vinculo
PATCH /aventureiros/{id}/recrutar

PUT   /aventureiros/{id}/companheiro
PATCH /aventureiros/{id}/companheiro
PATCH /aventureiros/{id}/remover-companheiro
```

### 🗡️ Missões

```
GET    /missoes
GET    /missoes?size=50
GET    /missoes/{id}
GET    /missoes/{id}/detalhe

PATCH  /missoes/{id}
DELETE /missoes/{id}

POST   /missoes/{id}/aventureiros/{id}/participacao
DELETE /missoes/{id}/aventureiros/{id}/participacao

GET    /missoes/top15dias
```

### 📊 Relatórios

```
GET /relatorios/ranking-participacao
GET /relatorios/missoes-metricas
```

**Exemplo:**

```
/relatorios/ranking-participacao?inicio=2000-01-01T00:00:00Z&fim=2100-01-01T00:00:00Z
```

### 🔎 Elasticsearch — Buscas

```
GET /produtos/busca/nome?termo=espada
GET /produtos/busca/descricao?termo=dragoes
GET /produtos/busca/frase
GET /produtos/busca/fuzzy
GET /produtos/busca/multicampos
GET /produtos/busca/com-filtro
GET /produtos/busca/faixa-preco
GET /produtos/busca/avancada
```

### 🔎 Elasticsearch — Agregações

```
GET /produtos/agregacoes/por-categoria
GET /produtos/agregacoes/por-raridade
GET /produtos/agregacoes/preco-medio
GET /produtos/agregacoes/faixas-preco
```

---

## 📬 Postman

Importe a collection no Postman para facilitar os testes.

---

## 🔄 Fluxo correto

1. Subir Docker (Postgres + Elastic + Redis)
2. Rodar aplicação
3. Executar inserts
4. Testar endpoints

---

## ⚠️ Problemas comuns

### ❌ Banco não conecta
- Porta `5432`
- Senha `guildpass`
- Container ativo

### ❌ Elasticsearch não responde
- Porta `9200`
- Container ativo

### ❌ Redis não funciona
- Porta `6379`
- Container ativo

### ❌ Sem dados na API
- Execute os inserts SQL

---

## 🧠 Observações

- O banco já vem estruturado pela imagem Docker
- O projeto **não** cria tabelas automaticamente
- Use `validate` após o setup inicial

---

## 👨‍💻 Autor

Projeto desenvolvido para fins acadêmicos e evolução profissional em backend.

---

## 📌 Status

- ✅ Em desenvolvimento
- ✅ Pronto para execução local
- ✅ Estrutura próxima de ambiente real

---

## 🚀 Resultado

Este projeto demonstra:

- Domínio de APIs REST
- Integração com múltiplas tecnologias
- Uso de cache (Redis)
- Busca avançada (Elasticsearch)
- Organização em camadas
- Preparação para ambiente real
