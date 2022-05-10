.PHONY: all clean 

#LDFLAGS := -lc -O0 -g3

TARGETS = busspeed linpack dhry fft1 fft3c linpackneon linpacksp lloops2 memspeed neonspeed whets

all: $(TARGETS)

%.o:%.c
	$(CC) -O3 -c $^ -o $@

busspeed: busspeed.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

linpack: linpack.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

dhry: dhry_1.o dhry_2.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

fft1: fft1.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

fft3c: fft3c.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

linpackneon: linpackneon.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

linpacksp: linpacksp.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

lloops2: lloops2.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

memspeed: memspeed.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

neonspeed: neonspeed.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

whets: whets.o cpuidc.o
	$(CC) $(LDFLAGS) -O3 -o $@ $^

clean:
	@rm -rf *.o $(TARGETS)
