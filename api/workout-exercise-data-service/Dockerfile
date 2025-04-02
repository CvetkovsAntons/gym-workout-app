FROM golang:1.24-alpine

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . ./

RUN cd ./cmd/api && CGO_ENABLED=0 GOOS=linux go build -o /app/gwa-api-workout-exercise-data-service

EXPOSE 8082

CMD ["./gwa-api-workout-exercise-data-service"]