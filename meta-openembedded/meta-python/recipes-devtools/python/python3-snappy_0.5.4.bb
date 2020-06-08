DESCRIPTION = "Python library for the snappy compression library from Google"
DEPENDS += "snappy"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3090152f53ee19f6a7b64b1a36384fb"

SRC_URI[sha256sum] = "d9c26532cfa510f45e8d135cde140e8a5603d3fb254cfec273ebc0ecf9f668e2"

inherit pypi setuptools3

PYPI_PACKAGE = "python-snappy"

RDEPENDS_${PN} += "snappy"

BBCLASSEXTEND = "native nativesdk"
