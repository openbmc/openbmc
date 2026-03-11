SUMMARY = "Run-time type checker for Python"
HOMEPAGE = "https://pypi.org/project/typeguard/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[sha256sum] = "3a7fd2dffb705d4d0efaed4306a704c89b9dee850b688f060a8b1615a79e5f74"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-core \
    python3-compression \
    python3-unittest \
    python3-typing-extensions \
"

RDEPENDS:${PN}-ptest += " \
    python3-typing-extensions \
    python3-unixadmin \
    python3-mypy \
"

DEPENDS += "\
    python3-distutils-extra-native \
    python3-setuptools-scm-native \
"

BBCLASSEXTEND = "native nativesdk"
