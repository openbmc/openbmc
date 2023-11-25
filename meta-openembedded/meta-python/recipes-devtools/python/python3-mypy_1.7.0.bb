SUMMARY = "Optional static typing for Python 3 and 2 (PEP 484)"
HOMEPAGE = "https://github.com/python/mypy"
LICENSE = "MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8d62fd8f8648cb018e52857347e340b9"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1e280b5697202efa698372d2f39e9a6713a0395a756b1c6bd48995f8d72690dc"

BBCLASSEXTEND = "native"

DEPENDS += " \
    python3-mypy-extensions-native \
    python3-types-psutil-native \
    python3-types-setuptools-native \
    python3-typing-extensions-native \
"

RDEPENDS:${PN} += " \
    python3-modules \
    python3-mypy-extensions \
    python3-typing-extensions \
"
