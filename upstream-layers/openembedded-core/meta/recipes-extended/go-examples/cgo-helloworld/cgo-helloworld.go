package main

// #include <stdio.h>
// void hello() { printf("Hello, world!\n"); fflush(stdout);}
import "C"

func main() {
	C.hello()
}
