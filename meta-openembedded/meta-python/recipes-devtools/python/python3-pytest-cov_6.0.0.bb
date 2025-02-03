SUMMARY = "Pytest plugin for measuring coverage."
HOMEPAGE = "https://github.com/pytest-dev/pytest-cov"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=cbc4e25353c748c817db2daffe605e43 \
"

SRC_URI[sha256sum] = "fde0b595ca248bb8e2d76f020b465f3b107c9632e6a1d1705f17834c89dcadc0"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-coverage python3-pytest"

BBCLASSEXTEND = "native nativesdk"
