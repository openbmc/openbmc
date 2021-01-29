DESCRIPTION = "Python library for the snappy compression library from Google"
DEPENDS += "snappy"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3090152f53ee19f6a7b64b1a36384fb"

SRC_URI[sha256sum] = "168a98d3f597b633cfeeae7fe1c78a8dfd81f018b866cf7ce9e4c56086af891a"

inherit pypi setuptools3

PYPI_PACKAGE = "python-snappy"

RDEPENDS_${PN} += "snappy"

BBCLASSEXTEND = "native nativesdk"
