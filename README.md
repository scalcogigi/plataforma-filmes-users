# Plataforma de Filmes — Serviço de Usuários

Este projeto implementa a parte de usuários da Plataforma de Filmes. A aplicação disponibiliza uma API REST para criar, consultar, atualizar e remover perfis de usuários, mantendo os dados persistidos em MongoDB.

## Tecnologias

- Java 24
- Spring Boot 3.5.5
- Spring Web e Spring Data MongoDB
- Maven Wrapper
- JUnit, Mockito e MockMvc para testes
- JaCoCo para cobertura

## Organização

- `controller`: endpoints HTTP da API (`/users`);
- `service`: regras de negócio;
- `repository`: acesso aos dados no MongoDB;
- `entity`: modelo persistido de usuário;
- `dto`: objetos de requisição e resposta;
- `exception`: exceções e tratamento global de erros;
- `config`: configurações da aplicação e do MongoDB.

## Executando localmente

Pré-requisitos: JDK 24 ou superior e MongoDB local ou uma URI válida.

Na raiz do projeto, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

Para executar os testes:

```powershell
.\mvnw.cmd clean test
```

No Linux/macOS, use `./mvnw` no lugar de `.\mvnw.cmd`.

## Configuração

As configurações padrão estão em `src/main/resources/application.properties`:

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017/plataforma-filmes` | URI do MongoDB |
| `SERVER_PORT` | `8080` | Porta HTTP |

Exemplo no PowerShell:

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/plataforma-filmes"
$env:SERVER_PORT = "8080"
.\mvnw.cmd spring-boot:run
```

## Endpoints principais

| Método | Rota | Objetivo |
| --- | --- | --- |
| `POST` | `/users` | Cria um usuário |
| `GET` | `/users` | Lista todos os usuários |
| `GET` | `/users/{id}` | Consulta um usuário pelo ID |
| `PUT` | `/users/{id}` | Atualiza um usuário |
| `DELETE` | `/users/{id}` | Remove um usuário |

Exemplo de criação:

```json
{
  "auth0UserId": "auth0|123456",
  "username": "usuario.demo",
  "email": "usuario@exemplo.com",
  "displayName": "Usuário Demo",
  "profileImageUrl": "https://exemplo.com/avatar.png",
  "bio": "Perfil de demonstração"
}
```

O cadastro responde com `201 Created`; consultas bem-sucedidas, com `200 OK`; a exclusão, com `204 No Content`. Recursos inexistentes são tratados como `404 Not Found`.

## Testes e cobertura

Requer JDK 24 ou superior configurado em `JAVA_HOME`. O Maven Wrapper está incluído.

Na pasta `plataforma-filmes-users`, execute:

```powershell
.\mvnw.cmd clean test
```

No Linux/macOS: `sh mvnw clean test`. Com Maven instalado: `mvn clean test`.
A primeira execução precisa de acesso à internet para baixar o Maven e as dependências.

Abra **[tests/index.html](tests/index.html)** para consultar a cobertura. O JaCoCo também
gera `tests/jacoco.xml` e `tests/jacoco.csv`; o resultado dos testes fica em
`target/surefire-reports/`. Esses arquivos são gerados localmente e ignorados pelo Git.

O padrão segue `projeto-software-2026-2-pagamento`: JUnit, Mockito nos serviços,
MockMvc nos controllers e JaCoCo 0.8.15 com relatório na fase `test`, em `tests/`.
Nenhuma classe da aplicação foi excluída da medição. Não há limite mínimo no Maven,
assim como no projeto de referência; o workflow existente mantém sua verificação de 80%
e passa a ler `tests/jacoco.xml`.

### O que é verificado

- Serviços: criação, busca, listagem, atualização do perfil e exclusão, incluindo IDs inexistentes.
- Atualização: preservação do ID, Auth0, relacionamentos e datas conforme o comportamento atual.
- API: ciclo completo por HTTP com persistência, serialização dos campos e respostas 201, 200, 204 e 404.
- Erros inesperados: resposta 500 sem exposição de detalhes internos.
- Repository: persistência do perfil completo, consultas por Auth0/username, atualização e exclusão.

O perfil `test` usa um servidor [mongo-java-server](https://github.com/bwaldvogel/mongo-java-server)
em memória, com porta local dinâmica e encerramento pelo Spring. Não requer Docker nem
MongoDB externo. Trata-se de uma implementação do protocolo MongoDB para testes;
esses testes não substituem a validação contra a versão de MongoDB usada em produção.
As configurações de produção permanecem em `src/main/resources/application.properties`.