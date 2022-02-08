SUMMARY = "WIM Protocol plugin for ICQ for Adium, Pidgin, Miranda and Telepathy IM Framework"
SECTION = "webos/services"
LICENSE = "GPLv3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = "pidgin json-glib"

PV = "0.1+gitr${SRCPV}"

inherit pkgconfig

SRC_URI = "git://github.com/EionRobb/icyque;branch=master;protocol=https"
SRCREV = "513fc162d5d1a201c2b044e2b42941436d1069d5"

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake;
}

do_install() {
    oe_runmake DESTDIR="${D}" install;
}

FILES_${PN} += " \
    ${libdir} \
"
