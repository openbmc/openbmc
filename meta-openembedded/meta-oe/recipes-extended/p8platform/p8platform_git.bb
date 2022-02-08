SUMMARY = "Platform support library used by libCEC and binary add-ons for Kodi"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://src/os.h;md5=752555fa94e82005d45fd201fee5bd33"

PV = "2.1.0.1"

SRC_URI = "git://github.com/Pulse-Eight/platform.git;branch=master;protocol=https \
           file://0001-Make-resulting-cmake-config-relocatable.patch"
SRCREV = "2d90f98620e25f47702c9e848380c0d93f29462b"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE += "-DCMAKE_INSTALL_LIBDIR=${libdir} -DCMAKE_INSTALL_LIBDIR_NOARCH=${libdir}"

FILES_${PN}-dev += "${libdir}/p8-platform"
