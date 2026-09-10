SUMMARY = "A Python object API for managing the Linux LIO kernel target"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=34400b68072d710fecd0a2940a0d1658"
DEPENDS = "python3-hatchling-native python3-hatch-vcs-native"

inherit pypi python_hatchling

PYPI_PACKAGE = "rtslib_fb"

SRC_URI[sha256sum] = "0084daa651a72b1cacd0eaaf162725df69b991f501cffe87e0f67e99224a7267"

RDEPENDS:${PN} = "python3-pyudev"
