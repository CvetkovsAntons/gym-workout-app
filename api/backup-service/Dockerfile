FROM golang:1.24-alpine

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . ./

RUN cd ./cmd/api && CGO_ENABLED=0 GOOS=linux go build -o /app/gwa-api-backup-service

EXPOSE 8081

CMD ["./gwa-api-backup-service"]