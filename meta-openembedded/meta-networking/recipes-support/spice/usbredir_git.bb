SUMMARY = "usbredir libraries and utilities"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4b54a1fd55a448865a0b32d41598759d \
"

DEPENDS = "libusb1"

SRCREV = "39aa3c69f61bba28856a3eef3fe4ab37a3968e88"
PV = "0.7.1+git${SRCPV}"

SRC_URI = " \
    git://anongit.freedesktop.org/spice/usbredir \
"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
