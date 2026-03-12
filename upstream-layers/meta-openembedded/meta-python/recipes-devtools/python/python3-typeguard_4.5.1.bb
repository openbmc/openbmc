SUMMARY = "Run-time type checker for Python"
HOMEPAGE = "https://pypi.org/project/typeguard/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[sha256sum] = "f6f8ecbbc819c9bc749983cc67c02391e16a9b43b8b27f15dc70ed7c4a007274"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-core \
    python3-compression \
    python3-unittest \
    python3-typing-extensions \
    python3-json \
"

RDEPENDS:${PN}-ptest += " \
    python3-typing-extensions \
    python3-unixadmin \
    python3-mypy \
    python3-pathspec \
"

DEPENDS += "\
    python3-distutils-extra-native \
    python3-setuptools-scm-native \
"

BBCLASSEXTEND = "native nativesdk"
