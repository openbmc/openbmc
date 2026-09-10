DESCRIPTION = "The Lemon Parser Generator"
HOMEPAGE = "https://sqlite.org/src/file/doc/lemon.html"
LICENSE = "LicenseRef-PD"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://tool/lemon.c;endline=8;md5=c7551a78fa3fdecd96d1ad6761d205ee"

SRC_URI = "git://github.com/sqlite/sqlite;protocol=https;branch=branch-3.53"

SRCREV = "b09c88c14082339b66c7b7158d609a771e64ca69"


do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} tool/lemon.c -o lemon
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 lemon ${D}${bindir}
    install -m 0644 tool/lempar.c ${D}${bindir}
}

BBCLASSEXTEND = "native nativesdk"
