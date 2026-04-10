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
- **DTO** → transporte de dados  

### 🔗 Integrações

- PostgreSQL → persistência  
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

## 🚀 Tecnologias

- Java 21  
- Spring Boot 3.4.3  
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

- Java 21+  
- Maven  
- Docker + Docker Compose  
- Postman (opcional)  

---

## ⚙️ Configuração

### 🔹 Datasource

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=guildpass

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.elasticsearch.uris=http://localhost:9200
spring.cache.type=redis
⚠️ Hibernate (IMPORTANTE)
🔹 Primeira execução
spring.jpa.hibernate.ddl-auto=update
🔹 Após a primeira execução
spring.jpa.hibernate.ddl-auto=validate
❗ Regra importante
Use update apenas na primeira execução
Depois utilize sempre validate
💡 Motivo
update → cria/ajusta estrutura automaticamente
validate → apenas valida (mais seguro)
🐳 Docker
🔹 Subir Elasticsearch + Kibana + Redis
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

  kibana:
    image: docker.elastic.co/kibana/kibana:9.2.5
    container_name: kibana-tp3
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    ports:
      - "5601:5601"

  redis-tp3:
    image: redis:latest
    container_name: redis-cache-tp3
    ports:
      - "6379:6379"
▶️ Subir serviços
docker-compose up -d
🔹 PostgreSQL (imagem do professor)
docker run -d --name postgres-tp2 \
-e POSTGRES_PASSWORD=guildpass \
-p 5432:5432 \
leogloriainfnet/postgres-tp2-spring:2.0-mac
🔹 Ajustar senha do banco
docker exec -it <ID_DO_CONTAINER> psql -U postgres
ALTER USER postgres WITH PASSWORD 'guildpass';
▶️ Rodando a aplicação
mvn spring-boot:run
🌐 Acesso

http://localhost:8080

🗄️ Popular banco

Execute os scripts na ordem:

aventureiros
companheiros
missoes
participacoes_missao
⚡ Teste rápido
curl http://localhost:8080/aventureiros
🔥 Endpoints
🧙 Aventureiros
GET /aventureiros
GET /aventureiros?size=100
GET /aventureiros/busca?trecho=Aegwynn
GET /aventureiros/{id}
GET /aventureiros/{id}/perfil

PATCH /aventureiros/{id}
PATCH /aventureiros/{id}/encerrar-vinculo
PATCH /aventureiros/{id}/recrutar

PUT /aventureiros/{id}/companheiro
PATCH /aventureiros/{id}/companheiro
PATCH /aventureiros/{id}/remover-companheiro
🗡️ Missões
GET /missoes
GET /missoes?size=50
GET /missoes/{id}
GET /missoes/{id}/detalhe

PATCH /missoes/{id}
DELETE /missoes/{id}

POST /missoes/{id}/aventureiros/{id}/participacao
DELETE /missoes/{id}/aventureiros/{id}/participacao

GET /missoes/top15dias
📊 Relatórios
GET /relatorios/ranking-participacao
GET /relatorios/missoes-metricas

Exemplo:

/relatorios/ranking-participacao?inicio=2000-01-01T00:00:00Z&fim=2100-01-01T00:00:00Z
🔎 Elasticsearch
Buscas
GET /produtos/busca/nome
GET /produtos/busca/descricao
GET /produtos/busca/frase
GET /produtos/busca/fuzzy
GET /produtos/busca/multicampos
GET /produtos/busca/com-filtro
GET /produtos/busca/faixa-preco
GET /produtos/busca/avancada
Agregações
GET /produtos/agregacoes/por-categoria
GET /produtos/agregacoes/por-raridade
GET /produtos/agregacoes/preco-medio
GET /produtos/agregacoes/faixas-preco
📬 Postman

Importe a collection para facilitar os testes.

🔄 Fluxo correto
Subir Docker
Rodar aplicação
Executar inserts
Testar endpoints
⚠️ Problemas comuns
❌ Banco não conecta
Porta 5432
Senha guildpass
Container ativo
❌ Elasticsearch não responde
Porta 9200
❌ Redis não funciona
Porta 6379
❌ Sem dados na API
Execute os inserts
🧠 Observações
O banco já vem estruturado pela imagem Docker
O projeto não cria tabelas automaticamente
Use validate após setup
👨‍💻 Autor

Projeto desenvolvido para fins acadêmicos e evolução profissional.

📌 Status
Em desenvolvimento
Pronto para testes locais
Estrutura de nível mercado

---

## 🔥 Resultado

✔ Igual ao layout da sua imagem  
✔ Títulos bonitos  
✔ Emojis organizando  
✔ Link clicável  
✔ Lista numerada certinha  
✔ Sem bug no GitHub  
