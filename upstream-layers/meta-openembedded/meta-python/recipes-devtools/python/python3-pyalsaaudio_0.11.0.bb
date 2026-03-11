SUMMARY = "ALSA bindings"
SECTION = "devel/python"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a3b161aa0fcec32a0c8907a2219ad9d"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a78a9dca33524b2c9064b34e21f5ab874272313cf324a9a77592f396a5e0fddc"

DEPENDS += "alsa-lib"

RDEPENDS:${PN} += "libasound"
