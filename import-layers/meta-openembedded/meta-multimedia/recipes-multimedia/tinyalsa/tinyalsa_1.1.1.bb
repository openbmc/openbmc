DESCRIPTION = "TinyALSA is a small library to interface with ALSA in \
the Linux kernel. It is a lightweight alternative to libasound."
HOMEPAGE = "https://github.com/tinyalsa/tinyalsa"
SECTION = "libs/multimedia"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=dbdefe400d894b510a9de14813181d0b"

S = "${WORKDIR}/git"
SRCREV = "df11091086b56e5fb71887f2fa320e1d2ffeff58"
SRC_URI = "git://github.com/tinyalsa/tinyalsa.git;protocol=https;"

do_configure() {
    :
}

do_compile() {
    oe_runmake CC='${CC}' LD='${CC}' AR='${AR}'
}

do_install() {
    oe_runmake install \
        PREFIX="${prefix}" DESTDIR="${D}" INCDIR="${includedir}/tinyalsa" \
        LIBDIR="${libdir}" BINDIR="${bindir}" MANDIR="${mandir}"
}

PACKAGES =+ "${PN}-tools"
FILES_${PN}-tools = "${bindir}/*"
