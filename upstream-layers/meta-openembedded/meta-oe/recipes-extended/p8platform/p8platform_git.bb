SUMMARY = "Platform support library used by libCEC and binary add-ons for Kodi"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=b3a719e97f49e4841e90573f9b1a98ac"

PV = "2.1.0.1+git"

SRC_URI = "git://github.com/Pulse-Eight/platform.git;branch=master;protocol=https"
SRCREV = "2748be52ae27e6007ef548b697d4a03ff7de4291"


inherit cmake pkgconfig

EXTRA_OECMAKE += "-DCMAKE_INSTALL_LIBDIR=${libdir} -DCMAKE_INSTALL_LIBDIR_NOARCH=${libdir}"

FILES:${PN}-dev += "${libdir}/p8-platform"

RDEPENDS:${PN}-dev = ""
