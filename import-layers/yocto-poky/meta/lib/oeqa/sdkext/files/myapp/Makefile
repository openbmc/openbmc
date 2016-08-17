all: myapp

myapp: myapp.o
	$(CC) $(LDFLAGS) $< -o $@

myapp.o: myapp.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf myapp.o myapp
