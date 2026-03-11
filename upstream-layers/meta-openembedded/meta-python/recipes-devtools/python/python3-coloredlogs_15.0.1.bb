DESCRIPTION = "Colored terminal output for Python's logging module"
HOMEPAGE = "https://coloredlogs.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=764e737b117a38d773609885e8d04f0b"

SRC_URI[sha256sum] = "7c991aa71a4577af2f82600d8f8f3a89f936baeaf9b50a9c197da014e5bf16b0"

inherit pypi setuptools3

do_install:append() {
    rm -rf ${D}${datadir}
}

RDEPENDS:${PN} += "\
    python3-humanfriendly \
"

BBCLASSEXTEND = "native nativesdk"
