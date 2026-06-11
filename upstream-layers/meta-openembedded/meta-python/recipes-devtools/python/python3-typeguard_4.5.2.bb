SUMMARY = "Run-time type checker for Python"
HOMEPAGE = "https://pypi.org/project/typeguard/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[sha256sum] = "5a16dcac23502039299c97c8941651bc33d7ea8cc4b2f7d6bbb1b528f6eea423"

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
