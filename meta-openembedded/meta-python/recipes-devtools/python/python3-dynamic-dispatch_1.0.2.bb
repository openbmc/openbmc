SUMMARY = "dynamic dispatch decorator for classes and functions"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea70b07c354e36056bd35e17c9c3face"

inherit pypi setuptools3

SRC_URI[md5sum] = "0e29d2afa806b9b87dadfbed6a0afd6d"
SRC_URI[sha256sum] = "ec7025b2890e7a882ceef95ff82fd154265136af930cab42533070b557d2a15d"

PYPI_PACKAGE = "dynamic_dispatch"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS_${PN} += "\
  python3-typing \
  python3-typeguard \
"

BBCLASSEXTEND = "native nativesdk"
