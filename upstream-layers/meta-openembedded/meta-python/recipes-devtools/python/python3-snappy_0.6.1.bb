DESCRIPTION = "Python library for the snappy compression library from Google"
DEPENDS += "snappy"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3090152f53ee19f6a7b64b1a36384fb"

SRC_URI[sha256sum] = "b6a107ab06206acc5359d4c5632bd9b22d448702a79b3169b0c62e0fb808bb2a"

inherit pypi setuptools3

PYPI_PACKAGE = "python-snappy"

RDEPENDS:${PN} += "snappy"
