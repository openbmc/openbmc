EXE     = obmc-phosphor-example-sdbus
OBJS    = $(EXE).o
DEPPKGS = libsystemd
CC      ?= $(CROSS_COMPILE)gcc
INCLUDES += $(shell pkg-config --cflags $(DEPPKGS))
LIBS += $(shell pkg-config --libs $(DEPPKGS))

%.o : %.c
	$(CC) -c $< $(CFLAGS) $(INCLUDES) -o $@
$(EXE): $(OBJS)
	$(CC) $^ $(LDFLAGS) $(LIBS) -o $@
clean:
	rm -f $(OBJS) $(EXE) *.o *.d
distclean: clean
	rm -f *.c~ *.h~ *.sh~ Makefile~ config.mk~
