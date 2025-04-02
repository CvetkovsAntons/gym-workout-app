package main

import (
	"fmt"
	"rsc.io/quote/v4"
)

func main() {
	fmt.Println("Hello backup-api")
	fmt.Println(quote.Go())
}
