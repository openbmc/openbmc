DESCRIPTION = "Package that provides access to GPIO and other IO\
functions on the Broadcom BCM 2835 chip, allowing access to the\
GPIO pins on the 26 pin IDE plug on the RPi board"
SECTION = "base"
HOMEPAGE = "http://www.open.com.au/mikem/bcm2835"
AUTHOR = "Mike McCauley (mikem@open.com.au)"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e49f4652534af377a713df3d9dec60cb"

COMPATIBLE_MACHINE = "^rpi$"

SRC_URI = "http://www.airspayce.com/mikem/bcm2835/bcm2835-${PV}.tar.gz"

SRC_URI[md5sum] = "9bd2d39bf4b3a9e81dce799ca51c826a"
SRC_URI[sha256sum] = "564920d205977d7e2846e434947708455d468d3a952feca9faef643abd03a227"

inherit autotools

do_compile:append() {
    # Now compiling the examples provided by the package
    mkdir -p ${B}/examples/spiram
    for file in `ls ${S}/examples`; do
        example="$file"
        if [ "$file" = "spiram" ]; then
	    # This includes a tiny library
            EXAMPLE_LDFLAGS="-L${B}/examples/spiram -lspiram"
            example="spiram_test"
            ${CC} ${CFLAGS} -c ${S}/examples/spiram/spiram.c -o ${B}/examples/spiram/libspiram.o -I${S}/src -I${S}/examples/spiram
            rm -f ${B}/examples/spiram/libspiram.a && ${BUILD_AR} crD ${B}/examples/spiram/libspiram.a ${B}/examples/spiram/libspiram.o
	fi
        ${CC} ${LDFLAGS} ${S}/examples/${file}/${example}.c -o ${B}/examples/${example} -Bstatic -L${B}/src -lbcm2835 ${EXAMPLE_LDFLAGS} -I${S}/src
    done
}

do_install:append() {
    install -d ${D}/${libdir}/${BPN}
    for example in $(find ${B}/examples -type f -maxdepth 1)
    do
        install -m 0755 ${example} ${D}/${libdir}/${BPN}
    done
}

PACKAGES += "${PN}-tests"

RDEPENDS:${PN}-dev = ""

FILES:${PN} = ""
FILES:${PN}-tests = "${libdir}/${BPN}"
FILES:${PN}-dbg += "${libdir}/${BPN}/.debug"
