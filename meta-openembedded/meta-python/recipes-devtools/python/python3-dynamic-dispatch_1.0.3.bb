SUMMARY = "dynamic dispatch decorator for classes and functions"
HOMEPAGE = "https://github.com/XevoInc/dynamic_dispatch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea70b07c354e36056bd35e17c9c3face"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fbc676aaedc8ec542056c21e5e206b8b62b8d11c3f3c5cfb32b273936da89604"

PYPI_PACKAGE = "dynamic_dispatch"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "\
  python3-typeguard \
"

BBCLASSEXTEND = "native nativesdk"
