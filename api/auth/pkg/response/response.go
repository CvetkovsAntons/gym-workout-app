package response

import (
	"encoding/json"
	"log"
	"net/http"
)

func WriteMessage(w http.ResponseWriter, status int, message string) {
	Write(w, status, map[string]string{"message": message})
}

func WriteError(w http.ResponseWriter, status int, error string, errors ...error) {
	Write(w, status, map[string]string{"error": error}, errors...)
}

func WriteInternalServerError(w http.ResponseWriter, errors ...error) {
	WriteError(w, http.StatusInternalServerError, "Internal server error", errors...)
}

func Write(w http.ResponseWriter, status int, response interface{}, errors ...error) {
	for _, e := range errors {
		log.Println(e)
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)

	err := json.NewEncoder(w).Encode(response)
	if err != nil {
		log.Println(err)
		WriteInternalServerError(w, err)
		return
	}
}
