SUMMARY = "Define boolean algebras, create and parse boolean expressions and create custom boolean DSL"
HOMEPAGE = "https://github.com/bastikr/boolean.py"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d118b5feceee598ebeca76e13395c2bd"

SRC_URI[sha256sum] = "60cbc4bad079753721d32649545505362c754e121570ada4658b852a3a318d95"

PYPI_PACKAGE = "boolean_py"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
