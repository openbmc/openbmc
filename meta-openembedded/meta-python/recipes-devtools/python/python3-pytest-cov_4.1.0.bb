SUMMARY = "Pytest plugin for measuring coverage."
HOMEPAGE = "https://github.com/pytest-dev/pytest-cov"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=cbc4e25353c748c817db2daffe605e43 \
"

SRC_URI[sha256sum] = "3904b13dfbfec47f003b8e77fd5b589cd11904a21ddf1ab38a64f204d6a10ef6"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-coverage python3-pytest"

BBCLASSEXTEND = "native nativesdk"
