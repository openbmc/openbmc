DESCRIPTION = "libftdi is a library to talk to FTDI chips.\
FT232BM/245BM, FT2232C/D and FT232/245R using libusb,\
including the popular bitbang mode."
HOMEPAGE = "http://www.intra2net.com/en/developer/libftdi/"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM= "\
    file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
"

DEPENDS = "libusb1"

SRC_URI = "http://www.intra2net.com/en/developer/${BPN}/download/${BPN}1-${PV}.tar.bz2"
SRC_URI[md5sum] = "0c09fb2bb19a57c839fa6845c6c780a2"
SRC_URI[sha256sum] = "ec36fb49080f834690c24008328a5ef42d3cf584ef4060f3a35aa4681cb31b74"

S = "${WORKDIR}/${BPN}1-${PV}"

inherit cmake binconfig pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[cpp-wrapper] = "-DFTDI_BUILD_CPP=on -DFTDIPP=on,-DFTDI_BUILD_CPP=off -DFTDIPP=off,boost"

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"

BBCLASSEXTEND = "native nativesdk"
