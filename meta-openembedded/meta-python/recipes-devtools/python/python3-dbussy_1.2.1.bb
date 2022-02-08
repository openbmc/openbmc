SUMMARY = "language bindings for libdbus, for Python 3.5 or later"
HOMEPAGE = "https://github.com/ldo/dbussy"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI = "git://github.com/ldo/dbussy.git;branch=master;protocol=https"

SRCREV = "d0ec0223f3797e1612d835e71694a1083881149f"

S = "${WORKDIR}/git"

inherit distutils3

RDEPENDS_${PN} += "\
    python3-asyncio \
    python3-core \
    python3-ctypes \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"

