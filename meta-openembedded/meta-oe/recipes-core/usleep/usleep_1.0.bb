SUMMARY = "A user tool to support sleeping some number of microseconds"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

S = "${WORKDIR}"
DEPENDS = "popt"

SRC_URI = "file://usleep.c \
    file://usleep.1 \
    file://GPLv2.patch \
"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} usleep.c -o usleep -lpopt
}

do_install() {
    install -d ${D}${base_bindir}
    install -d ${D}${mandir}/man1

    install -m 0755 ${WORKDIR}/usleep   ${D}${base_bindir}
    install -m 0644 ${WORKDIR}/usleep.1 ${D}${mandir}/man1
}
