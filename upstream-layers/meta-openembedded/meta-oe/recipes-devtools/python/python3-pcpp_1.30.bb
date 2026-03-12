SUMMARY = "A C99 preprocessor written in pure Python"
HOMEPAGE = "https://github.com/ned14/pcpp"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d3c12e3df3b040ebb89409b40ff32b3a"

SRC_URI[sha256sum] = "5af9fbce55f136d7931ae915fae03c34030a3b36c496e72d9636cedc8e2543a1"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-doctest python3-pickle"

BBCLASSEXTEND = "native"
