DESCRIPTION = "biossums tool for building Plex86/Bochs LGPL VGABios"
HOMEPAGE = "http://www.nongnu.org/vgabios/"
LICENSE = "LGPLv2.1"
SECTION = "firmware"

LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"

SRC_URI =  "http://savannah.gnu.org/download/vgabios/vgabios-${PV}.tgz"

SRC_URI[md5sum] = "2c0fe5c0ca08082a9293e3a7b23dc900"
SRC_URI[sha256sum] = "9d24c33d4bfb7831e2069cf3644936a53ef3de21d467872b54ce2ea30881b865"

BBCLASSEXTEND = "native"

FILES_${PN} = "${bindir}/biossums"

S = "${WORKDIR}/vgabios-${PV}"

do_configure() {
    # Don't override the compiler or its flags:
    sed 's,^CC,DISABLED_CC,' -i Makefile
    sed 's,^CFLAGS,DISABLED_CFLAGS,' -i Makefile
    sed 's,^LDFLAGS,DISABLED_LDFLAGS,' -i Makefile
    # Supply the C flags to the compiler:
    sed 's,-o biossums,$(CFLAGS) -o biossums,' -i Makefile
}

do_compile() {
    # clean removes binaries distributed with source
    oe_runmake clean
    oe_runmake biossums
}

do_install() {
    mkdir -p "${D}${bindir}"
    install -m 0755 biossums "${D}${bindir}"
}
