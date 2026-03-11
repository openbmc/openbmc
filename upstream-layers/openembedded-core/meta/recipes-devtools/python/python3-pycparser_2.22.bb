SUMMARY = "Parser of the C language, written in pure Python"
HOMEPAGE = "https://github.com/eliben/pycparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9761c3ffee7ba99c60dca0408fd3262b"

SRC_URI[sha256sum] = "491c8be9c040f5390f5bf44a5b07752bd07f56edf992381b05c701439eec10f6"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}:class-target += "\
    python3-netclient \
"

RSUGGESTS:${PN}:class-target += "\
    cpp \
    cpp-symlinks \
    "
