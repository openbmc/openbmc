# For native build cd_idt8, cd_create_profile and a tiny version of libcolord.so
# only. By reducing the library to what's necessary, we avoid dependencies on
# naitve udev and libgudev

CFLAGS+=$(shell pkg-config-native --cflags gio-2.0)
CFLAGS+=$(shell pkg-config-native --cflags lcms2)
CFLAGS+=-std=c99 -I./lib -I./lib/colord -I$(DESTDIR) -DCD_COMPILATION

LDFLAGS+=$(shell pkg-config-native --libs gio-2.0)
LDFLAGS+=$(shell pkg-config-native --libs lcms2)
LDFLAGS+=-lm

all: cd_idt8 cd_create_profile

LIBSOURCES = \
	lib/colord/cd-it8.c \
	lib/colord/cd-color.c \
	lib/colord/cd-spectrum.c \
	lib/colord/cd-math.c \
	lib/colord/cd-interp-akima.c \
	lib/colord/cd-context-lcms.c \
	lib/colord/cd-interp.c \
	lib/colord/cd-interp-linear.c \
	lib/colord/cd-it8-utils.c \
	lib/colord/cd-enum.c \
    lib/colord/cd-icc.c \
    lib/colord/cd-icc-utils.c \
    lib/colord/cd-dom.c

libcolord.so: $(LIBSOURCES)
	$(CC) $(CFLAGS) -L$(DESTDIR) -fPIC -shared -Wl,-soname,$@ $^ -o $(DESTDIR)/$@ $(LDFLAGS)

cd_idt8.o: client/cd-it8.c
	$(CC) $(CFLAGS) -c $^ -o $(DESTDIR)/$@

cd_idt8: libcolord.so cd_idt8.o
	$(CC) $(DESTDIR)/cd_idt8.o -o $(DESTDIR)/$@ -L$(DESTDIR) -lcolord $(LDFLAGS)

cd_create_profile.o: client/cd-create-profile.c
	$(CC) $(CFLAGS) -c $^ -o $(DESTDIR)/$@

cd_create_profile: libcolord.so cd_create_profile.o
	$(CC) $(DESTDIR)/cd_create_profile.o -o $(DESTDIR)/$@ -L$(DESTDIR) -lcolord $(LDFLAGS)

.PHONY: clean

clean:
	rm -f $(DESTDIR)/libcolord.so $(DESTDIR)/*.o $(DESTDIR)/cd_idt8 $(DESTDIR)/cd_create_profile
