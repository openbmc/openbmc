DESCRIPTION = "Cross-platform network interface and IP address enumeration \
library"
HOMEPAGE = "https://pypi.org/project/ifaddr/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8debe8d42320ec0ff24665319b625a5e"

SRC_URI[sha256sum] = "cc0cbfcaabf765d44595825fb96a99bb12c79716b73b44330ea38ee2b0c4aed4"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
"
