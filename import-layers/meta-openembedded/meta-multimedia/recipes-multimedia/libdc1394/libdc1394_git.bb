SUMMARY = "Capture and control API for IIDC compliant cameras"
HOMEPAGE = "http://sourceforge.net/projects/libdc1394/"
SECTION = "libs"
LICENSE = "LGPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c848e78d9a4a5cc69906178e4d6fbd64"

# libsdl to provide sdl.m4 with AM_PATH_SDL
DEPENDS += "libusb1 libraw1394 libsdl"

PV = "2.2.1+gitr${SRCPV}"

SRCREV = "b90342933b4b48634b98b73805f47bd25ed5857e"

SRC_URI = "git://git.code.sf.net/p/libdc1394/code;branch=master;protocol=git \
           file://install_examples.patch \
          "

S = "${WORKDIR}/git/${PN}"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF += "--disable-doxygen-doc --disable-examples"
