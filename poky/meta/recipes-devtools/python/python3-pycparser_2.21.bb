SUMMARY = "Parser of the C language, written in pure Python"
HOMEPAGE = "https://github.com/eliben/pycparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c28cdeabcb88f5843d934381b4b4fea"

SRC_URI[sha256sum] = "e644fdec12f7872f86c58ff790da456218b10f863970249516d60a5eaca77206"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-ply \
    ${PYTHON_PN}-pprint \
    "

RSUGGESTS:${PN}:class-target += "\
    cpp \
    cpp-symlinks \
    "
