# Hotel — CRUD com JDBC e MySQL

Backend REST (Java + Spring Boot) para cadastro de clientes e reservas de hotel.
O acesso ao banco é feito com **JDBC puro** (`Connection`, `PreparedStatement`, `ResultSet`) — sem JPA/Hibernate.

## Endpoints

| Método | Rota | O que faz |
|---|---|---|
| POST | `/api/reservas` | Cria a reserva (e o cliente, se o e-mail ainda não existir) |
| GET | `/api/reservas?email=...` | Lista as reservas do e-mail informado |
| GET | `/api/reservas/{id}` | Busca uma reserva |
| PUT | `/api/reservas/{id}` | Atualiza data de chegada, noites, hóspedes e mensagem |
| DELETE | `/api/reservas/{id}` | Exclui a reserva |
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/clientes/{id}` | Busca um cliente |
| PUT | `/api/clientes/{id}` | Atualiza nome, sexo e e-mail do cliente |
| DELETE | `/api/clientes/{id}` | Exclui o cliente **e as reservas dele** (`ON DELETE CASCADE`) |

Corpo do `POST /api/reservas`:

```json
{
  "nome": "Ana Silva",
  "sexo": "Feminino",
  "email": "ana.silva@example.com",
  "dataChegada": "2027-01-20",
  "numeroNoites": 3,
  "numeroHospedes": 2,
  "mensagem": "Quarto silencioso, por favor."
}
```

Regras: o e-mail identifica o cliente (guardado em minúsculas). Se já existir cliente com esse e-mail, ele é reaproveitado e nome/sexo são atualizados. Datas no formato `AAAA-MM-DD`. Erros voltam em JSON: `400` (dados inválidos), `404` (ID não existe), `409` (e-mail já usado por outro cliente).

## Pré-requisitos (macOS)

1. **Java 17 ou superior** — `java -version`. Se não tiver: `brew install openjdk@21` e
   `export JAVA_HOME=$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home`
2. **MySQL na porta 3306 com usuário `root` / senha `root`** — escolha uma opção:

**Opção A — Docker**
```bash
docker compose up -d
```

**Opção B — Homebrew**
```bash
brew install mysql
brew services start mysql
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root'; FLUSH PRIVILEGES;"
```

Se o seu MySQL já tem outra senha/usuário, não precisa mudar nada no código:
```bash
export DB_USER='root'
export DB_PASSWORD='sua_senha'
```

O banco `hotel_db` e as tabelas `clientes` e `reservas` são criados automaticamente ao iniciar (`schema.sql`).

## Rodar a API

```bash
cd hotel-reservas
./mvnw spring-boot:run
```

(Na primeira vez baixa as dependências. Se der "permission denied": `chmod +x mvnw`.)
Quando aparecer `Started HotelApplication`, a API está em `http://localhost:8080`.

## Testar no Postman

1. Postman → **Import** → `postman/Hotel-CRUD-JDBC.postman_collection.json`
2. Rode as requisições **na ordem, de 1 a 10** (a 1 grava `reservaId` e `clienteId` automaticamente; a coleção tem a variável `baseUrl` = `http://localhost:8080`).
3. Dica: botão **Run collection** executa tudo e mostra os testes passando.

No Insomnia dá para importar o mesmo arquivo (Import → From File).

## O que mostrar como resultado (prints)

- Teste 1 — POST criar reserva (`201`)
- Teste 2 — GET por e-mail (`200` com a lista)
- Teste 4 — PUT atualizar reserva
- Teste 5 — PUT atualizar cliente
- Teste 7 e 9 — DELETE reserva e DELETE cliente (`204`)
- Dados gravados no MySQL: `mysql -u root -proot hotel_db -e "SELECT * FROM clientes; SELECT * FROM reservas;"` (rode antes dos DELETEs)

## Estrutura

```text
src/main/java/br/com/hotel/reservas/
├── controller/  rotas REST
├── service/     regras e transação (commit/rollback)
├── dao/         SQL com JDBC puro
├── dto/         corpos das requisições + validação
├── model/       Cliente e Reserva
└── exception/   erros HTTP em JSON
```
