SUMMARY = "Parser of the C language, written in pure Python"
HOMEPAGE = "https://github.com/eliben/pycparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c453675224ee9a35b46135214d26c055"

SRC_URI[sha256sum] = "f03387877eab82ce01bbdfd0ba7252ee55a90b1c708d5129e2ce915475cf1db6"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN}:class-target += "\
    python3-netclient \
"

RSUGGESTS:${PN}:class-target += "\
    cpp \
    cpp-symlinks \
    "
