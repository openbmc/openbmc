EXE     = obmc-phosphor-fand
OBJS    = $(EXE).o
DEPPKGS = gio-unix-2.0 glib-2.0
CC      ?= $(CROSS_COMPILE)gcc
INCLUDES += $(shell pkg-config --cflags $(DEPPKGS))
LIBS += $(shell pkg-config --libs $(DEPPKGS))

%.o : %.c
	$(CC) -c $^ $(CFLAGS) $(INCLUDES) -o $@
$(EXE): $(OBJS)
	$(CC) $^ $(LDFLAGS) $(LDFLAGS) -o $@
clean:
	rm -f $(OBJS) $(EXE) *.o *.d
distclean: clean
	rm -f *.c~ *.h~ *.sh~ Makefile~ config.mk~
