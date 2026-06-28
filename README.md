# Spring RabbitMQ Orders

A microservice demonstrating event-driven order processing with Spring Boot and RabbitMQ.

## What it does

- Accepts orders via REST API
- Publishes an event to RabbitMQ on each order creation
- Consumes the event asynchronously and marks the order as `PROCESSED`
- Retries failed messages up to 3 times with exponential backoff
- Routes exhausted messages to a dead-letter queue
- Deduplicates events using a `processed_events` table


## Tech Stack

- Java 21
- Spring Boot 3.3.5
- RabbitMQ (Topic Exchange, DLX/DLQ, retry with backoff)
- PostgreSQL (JPA/Hibernate)
- Docker Compose

## Architecture

```
POST /orders
     │
     ▼
OrderController → OrderService → OrderRepository (Postgres)
                       │
                       ▼
               OrderEventPublisher
                       │
                       ▼
             orders.exchange (Topic)
                       │
                       ▼
               orders.queue
                       │
                  [on failure]
                       │
                       ▼
              orders.dlx → orders.dlq
```

## Running locally

**Start infrastructure:**

```bash
docker compose up -d
```

**Start the app:**

```bash
mvn spring-boot:run
```

**Create an order:**

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"product":"Laptop","quantity":1,"price":"999.99"}'
```

**Get an order:**

```bash
curl http://localhost:8080/orders/{id}
```

**Trigger dead-letter path:**

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"product":"FAIL","quantity":1,"price":"9.99"}'
```

## Running tests

```bash
mvn test
```

Requires docker compose services to be running for the integration test.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/orders` | Create an order |
| `GET` | `/orders/{id}` | Get an order by ID |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/metrics` | Metrics |

## RabbitMQ Management UI

Available at [http://localhost:15672](http://localhost:15672) (guest / guest) when running via Docker Compose.

## License

[MIT](LICENSE)
