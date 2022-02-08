SUMMARY = "Libuio - helper library for UIO subsystem"
SECTION = "base"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "git://git.code.sf.net/p/libuio/code;branch=master \
           file://replace_inline_with_static-inline.patch \
           file://0001-include-fcntl.h-for-O_RDWR-define.patch \
           "

inherit autotools

SRCREV = "ed4f07ea147ac403c28105ab44d01bbf524d36f9"

PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGES += "${PN}-tools"

FILES_${PN} = "${libdir}"
FILES_${PN}-tools = "${bindir}"
