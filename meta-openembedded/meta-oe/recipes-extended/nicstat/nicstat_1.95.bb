DESCRIPTION = "nicstat is a Solaris and Linux command-line that prints out network \
statistics for all network interface cards (NICs), including packets, kilobytes \
per second, average packet sizes and more."
HOMEPAGE = "http://nicstat.sourceforge.net"
LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b4a94da2a1f918b217ef5156634fc9e0"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://0001-nicstat.c-Do-not-define-uint64_t-and-uint32_t.patch \
           "
SRC_URI[md5sum] = "9a0b87bbc670c1e738e5b40c7afd184d"
SRC_URI[sha256sum] = "c4cc33f8838f4523f27c3d7584eedbe59f4c587f0821612f5ac2201adc18b367"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -o nicstat nicstat.c
}
do_install() {
    install -d ${D}/${bindir}/
    install -d ${D}/${mandir}/
    install -m 0755 ${S}/nicstat ${D}${bindir}/
    install -m 0644 ${S}/nicstat.1 ${D}/${mandir}/
}
