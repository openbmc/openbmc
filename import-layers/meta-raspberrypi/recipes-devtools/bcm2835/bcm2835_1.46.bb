DESCRIPTION = "Package that provides access to GPIO and other IO\
functions on the Broadcom BCM 2835 chip, allowing access to the\
GPIO pins on the 26 pin IDE plug on the RPi board"
SECTION = "base"
HOMEPAGE = "http://www.open.com.au/mikem/bcm2835"
AUTHOR = "Mike McCauley (mikem@open.com.au)"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

COMPATIBLE_MACHINE = "raspberrypi"

SRC_URI = "http://www.airspayce.com/mikem/bcm2835/bcm2835-${PV}.tar.gz"

SRC_URI[md5sum] = "3a40c01ee7d81fbff80c54fbe1a351b5"
SRC_URI[sha256sum] = "fc6b0412525e6b7e85aeffec67e2d01a99fb906346620041e6684d59ea5517a7"

inherit autotools

do_compile_append() {
    # Now compiling the examples provided by the package
    mkdir -p ${B}/examples
    for file in `ls ${S}/examples`; do
        ${CC} ${S}/examples/${file}/${file}.c -o ${B}/examples/${file} -Bstatic -L${B}/src -lbcm2835 -I${S}/src
    done
}

do_install_append() {
    install -d ${D}/${libdir}/${BPN}
    for file in ${B}/examples/*
    do
        install -m 0755 ${file} ${D}/${libdir}/${BPN}
    done
}

PACKAGES += "${PN}-tests"

FILES_${PN} = ""
FILES_${PN}-tests = "${libdir}/${BPN}"
FILES_${PN}-dbg += "${libdir}/${BPN}/.debug"
