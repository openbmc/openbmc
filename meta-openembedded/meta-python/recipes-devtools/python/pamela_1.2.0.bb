DESCRIPTION = "Pamela: yet another Python wrapper for PAM"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=b5fa9af58a8076d81145be56b6801a2b"

SRC_URI[sha256sum] = "0ea6e2a99dded8c7783a4a06f2d31f5bdcad894d79101e8f09322e387a34aacf"

PYPI_PACKAGE = "pamela"

inherit pypi setuptools3

RDEPENDS:${PN} = "libpam"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"
