SUMMARY = "Parser of the C language, written in pure Python"
HOMEPAGE = "https://github.com/eliben/pycparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86f1cedb4e6410a88ce8e30b91079169"

SRC_URI[md5sum] = "72370da54358202a60130e223d488136"
SRC_URI[sha256sum] = "99a8ca03e29851d96616ad0404b4aad7d9ee16f25c9f9708a11faf2810f7b226"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-ply \
    ${PYTHON_PN}-pprint \
    cpp \
    cpp-symlinks \
    "
