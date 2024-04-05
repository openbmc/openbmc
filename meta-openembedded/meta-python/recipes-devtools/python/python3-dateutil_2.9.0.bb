SUMMARY = "Extensions to the standard Python datetime module"
DESCRIPTION = "The dateutil module provides powerful extensions to the datetime module available in the Python standard library."
HOMEPAGE = "https://dateutil.readthedocs.org"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3155c7bdc71f66e02678411d2abf996"

SRC_URI[sha256sum] = "78e73e19c63f5b20ffa567001531680d939dc042bf7850431877645523c66709"

PYPI_PACKAGE = "python-dateutil"
inherit pypi python_setuptools_build_meta

PACKAGES =+ "${PN}-zoneinfo"
FILES:${PN}-zoneinfo = "${libdir}/${PYTHON_DIR}/site-packages/dateutil/zoneinfo"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} = "\
    python3-datetime \
    python3-numbers \
    python3-six \
    python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
