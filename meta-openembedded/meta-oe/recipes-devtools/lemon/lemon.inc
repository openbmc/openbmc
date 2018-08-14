DESCRIPTION = "The Lemon Parser Generator"
HOMEPAGE = "http://www.hwaci.com/sw/lemon/"
LICENSE = "PD"
SECTION = "devel"

SRC_URI = "http://www.sqlite.org/sqlite-${PV}.tar.gz \
           file://lemon.1"

S = "${WORKDIR}/sqlite-${PV}/tool"

do_compile() {
    ${CC} ${CFLAGS} lemon.c -c -o lemon.o
    ${CCLD} ${LDFLAGS} lemon.o -o lemon
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 lemon ${D}${bindir}
    install -m 0644 lempar.c ${D}${bindir}
    install -d ${D}${mandir}/man1
    install -m 0644 ${WORKDIR}/lemon.1 ${D}${mandir}/man1/
}

BBCLASSEXTEND = "native"

