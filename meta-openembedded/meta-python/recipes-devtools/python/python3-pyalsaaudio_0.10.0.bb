SUMMARY = "ALSA bindings"
SECTION = "devel/python"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a3b161aa0fcec32a0c8907a2219ad9d"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e21175500a2bd310ae3867e7991639defc1e2a5c92cf1b9f7083296b346738ab"

DEPENDS += "alsa-lib"

RDEPENDS:${PN} += "libasound"
