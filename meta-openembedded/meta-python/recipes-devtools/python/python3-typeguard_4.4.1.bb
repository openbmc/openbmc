SUMMARY = "Run-time type checker for Python"
HOMEPAGE = "https://pypi.org/project/typeguard/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[sha256sum] = "0d22a89d00b453b47c49875f42b6601b961757541a2e1e0ef517b6e24213c21b"

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
