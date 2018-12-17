DESCRIPTION = "Pamela: yet another Python wrapper for PAM"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=bfb663f37eb99232bc8ccfa4ea8f1202"

SRC_URI[md5sum] = "de6516118d51eb5fc97017f3b6d5c68b"
SRC_URI[sha256sum] = "1e198446a6cdd87704aa0def7621d62e7c20b0e6068e2788b9a866a8355e5d6b"

PYPI_PACKAGE = "pamela"

inherit pypi setuptools

RDEPENDS_${PN} = "libpam"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "pam"
