SUMMARY = "language bindings for libdbus, for Python 3.5 or later"
HOMEPAGE = "https://github.com/ldo/dbussy"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI = "git://github.com/ldo/dbussy.git"

SRCREV = "37ede4242b48def73ada46c2747a4c5cae6abf45"

S = "${WORKDIR}/git"

inherit distutils3

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-core \
    python3-ctypes \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"

