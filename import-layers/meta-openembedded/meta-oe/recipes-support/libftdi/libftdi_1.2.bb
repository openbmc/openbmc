DESCRIPTION = "libftdi is a library to talk to FTDI chips.\
FT232BM/245BM, FT2232C/D and FT232/245R using libusb,\
including the popular bitbang mode."
HOMEPAGE = "http://www.intra2net.com/en/developer/libftdi/"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM= "\
    file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LIB;md5=db979804f025cf55aabec7129cb671ed \
"

DEPENDS = "libusb1"

SRC_URI = "http://www.intra2net.com/en/developer/${BPN}/download/${BPN}1-${PV}.tar.bz2"

SRC_URI[md5sum] = "89dff802d89c4c0d55d8b4665fd52d0b"
SRC_URI[sha256sum] = "a6ea795c829219015eb372b03008351cee3fb39f684bff3bf8a4620b558488d6"

S = "${WORKDIR}/${BPN}1-${PV}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cpp-wrapper] = "-DFTDI_BUILD_CPP=on -DFTDIPP=on,-DFTDI_BUILD_CPP=off -DFTDIPP=off,boost"

inherit cmake binconfig pkgconfig

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib', True).replace('lib', '')}"

FILES_${PN}-dev += "${libdir}/cmake"

BBCLASSEXTEND = "native"
