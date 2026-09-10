SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36a13c90514e2899f1eba7f41c3ee592"

inherit python_hatchling
SRC_URI[sha256sum] = "610ca751c9bc2492b38eb9a38a7fbc93edbbb2d7182edaf34e66ae493dee5c8c"

RDEPENDS:${PN} += "python3-html"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

