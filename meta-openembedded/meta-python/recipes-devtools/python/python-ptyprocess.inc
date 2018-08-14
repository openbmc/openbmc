SUMMARY = "Run a subprocess in a pseudo terminal"
HOMEPAGE = "http://ptyprocess.readthedocs.io/en/latest/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cfdcd51fa7d5808da4e74346ee394490"

SRCNAME = "ptyprocess"

SRC_URI[md5sum] = "d3b8febae1b8c53b054bd818d0bb8665"
SRC_URI[sha256sum] = "e64193f0047ad603b71f202332ab5527c5e52aa7c8b609704fc28c0dc20c4365"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/ptyprocess"

S = "${WORKDIR}/${SRCNAME}-${PV}"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-core \
"

BBCLASSEXTEND = "native nativesdk"
