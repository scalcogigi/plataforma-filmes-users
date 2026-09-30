# Plataforma de filmes — Users

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
