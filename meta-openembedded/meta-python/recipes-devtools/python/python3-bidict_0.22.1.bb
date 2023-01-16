SUMMARY = "The bidirectional mapping library for Python."
DESCRIPTION = "The bidirectional mapping library for Python."
HOMEPAGE = "https://bidict.readthedocs.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=858e74278ef3830d46481172ae03c819"

SRC_URI[sha256sum] = "1e0f7f74e4860e6d0943a05d4134c63a2fad86f3d4732fb265bd79e4e856d81d"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
