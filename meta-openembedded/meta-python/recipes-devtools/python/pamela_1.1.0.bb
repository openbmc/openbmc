DESCRIPTION = "Pamela: yet another Python wrapper for PAM"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=6b706db92112b8384848de3e5c6adaa3"

SRC_URI[sha256sum] = "d4b139fe600e192e176a2a368059207a6bffa0e7879879b13f4fcba0163481be"

PYPI_PACKAGE = "pamela"

inherit pypi setuptools3

RDEPENDS:${PN} = "libpam"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"
