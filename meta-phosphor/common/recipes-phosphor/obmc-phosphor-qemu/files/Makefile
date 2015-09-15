EXE     = obmc-phosphor-qemu
OBJS    = $(EXE).o
PACKAGES= gio-unix-2.0 glib-2.0
INCLUDES += $(shell pkg-config --cflags $(PACKAGES))
LIBS += $(shell pkg-config --libs $(PACKAGES))
CC      = $(CROSS_COMPILE)gcc

%.o : %.c
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@
$(EXE): $(OBJS)
	$(CC) $(LDFLAGS) $(LIBS) $^ -o $@
clean:
	rm -f $(OBJS) $(EXE) *.o *.d
distclean: clean
	rm -f *.c~ *.h~ *.sh~ Makefile~ config.mk~
