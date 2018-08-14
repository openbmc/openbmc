SUMMARY = "Platform support library used by libCEC and binary add-ons for Kodi"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://src/os.h;md5=752555fa94e82005d45fd201fee5bd33"

PV = "2.1.0"

SRC_URI = "git://github.com/Pulse-Eight/platform.git"
SRCREV = "d7bceb64541cb046421cbcd4c98d91e9bf24822f"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE += "-DCMAKE_INSTALL_LIBDIR=${libdir} -DCMAKE_INSTALL_LIBDIR_NOARCH=${libdir}"

FILES_${PN}-dev += "${libdir}/p8-platform"
