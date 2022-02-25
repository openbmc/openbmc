DESCRIPTION = "Package that provides access to GPIO and other IO\
functions on the Broadcom BCM 2835 chip, allowing access to the\
GPIO pins on the 26 pin IDE plug on the RPi board"
SECTION = "base"
HOMEPAGE = "http://www.open.com.au/mikem/bcm2835"
AUTHOR = "Mike McCauley (mikem@open.com.au)"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

COMPATIBLE_MACHINE = "^rpi$"

SRC_URI = "http://www.airspayce.com/mikem/bcm2835/bcm2835-${PV}.tar.gz"

SRC_URI[md5sum] = "b5dc426b4ff258bb1397442f98e40236"
SRC_URI[sha256sum] = "b9fd10f7a80aadaed28a77168709b7c519568a63b6e98d0a50e9c5fe31bea6bb"

inherit autotools

do_compile:append() {
    # Now compiling the examples provided by the package
    mkdir -p ${B}/examples
    for file in `ls ${S}/examples`; do
        ${CC} ${LDFLAGS} ${S}/examples/${file}/${file}.c -o ${B}/examples/${file} -Bstatic -L${B}/src -lbcm2835 -I${S}/src
    done
}

do_install:append() {
    install -d ${D}/${libdir}/${BPN}
    for file in ${B}/examples/*
    do
        install -m 0755 ${file} ${D}/${libdir}/${BPN}
    done
}

PACKAGES += "${PN}-tests"

RDEPENDS:${PN}-dev = ""

FILES:${PN} = ""
FILES:${PN}-tests = "${libdir}/${BPN}"
FILES:${PN}-dbg += "${libdir}/${BPN}/.debug"
