SUMMARY = "Command line utility to extend hash of arbitrary data into a TPMs PCR."
HOMEPAGE = "https://github.com/flihp/pcr-extend"
SECTION = "security/tpm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libtspi"

PV = "0.1+git${SRCPV}"
SRCREV = "c02ad8f628b3d99f6d4c087b402fe31a40ee6316"

SRC_URI = "git://github.com/flihp/pcr-extend.git \
           file://fix_openssl11_build.patch "

inherit autotools

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake -C ${S}/src
}

do_install() {
    install -d ${D}${bindir}
    oe_runmake -C ${S}/src DESTDIR="${D}" install 
}
