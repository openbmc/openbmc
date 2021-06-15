DESCRIPTION = "Pamela: yet another Python wrapper for PAM"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=3f9b78307cdde4e6a4976bfd82a0e7f3"

SRC_URI[md5sum] = "5fc14f5275383ed8bdd509007af0323d"
SRC_URI[sha256sum] = "65c9389bef7d1bb0b168813b6be21964df32016923aac7515bdf05366acbab6c"

PYPI_PACKAGE = "pamela"

inherit pypi setuptools3

RDEPENDS_${PN} = "libpam"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"
