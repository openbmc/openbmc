SUMMARY = "Linux C libraries and Python modules for manipulating GPIO"
DESCRIPTION = "lgpio is a library for Linux Single Board Computers\
(SBC) which allows control of the General Purpose Input Outputs (GPIO)."
HOMEPAGE = "https://github.com/joan2937/lg"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://UNLICENCE;md5=61287f92700ec1bdf13bc86d8228cd13"

SRC_URI = "git://github.com/joan2937/lg.git;protocol=https;branch=master"
SRCREV = "bcccd782eceedc5b278b3056ea81d5fbbb89c489"

EXTRA_OEMAKE = "CC='${CC}' AR='${AR}' RANLIB='${RANLIB}' STRIP=true"

SOVERSION = "1"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/lgpio.h ${D}${includedir}/
    install -m 0644 ${S}/rgpio.h ${D}${includedir}/

    install -d ${D}${libdir}
    install -m 0755 ${S}/liblgpio.so.${SOVERSION} ${D}${libdir}/
    install -m 0755 ${S}/librgpio.so.${SOVERSION} ${D}${libdir}/
    ln -sf liblgpio.so.${SOVERSION} ${D}${libdir}/liblgpio.so
    ln -sf librgpio.so.${SOVERSION} ${D}${libdir}/librgpio.so

    install -d ${D}${bindir}
    install -m 0755 ${S}/rgpiod ${D}${bindir}/
    install -m 0755 ${S}/rgs ${D}${bindir}/

    install -d ${D}${mandir}/man1
    install -m 0644 ${S}/rgpiod.1 ${D}${mandir}/man1/
    install -m 0644 ${S}/rgs.1 ${D}${mandir}/man1/

    install -d ${D}${mandir}/man3
    install -m 0644 ${S}/lgpio.3 ${D}${mandir}/man3/
    install -m 0644 ${S}/rgpio.3 ${D}${mandir}/man3/
}
