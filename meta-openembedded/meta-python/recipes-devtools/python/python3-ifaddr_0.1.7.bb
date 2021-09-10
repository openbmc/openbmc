DESCRIPTION = "Cross-platform network interface and IP address enumeration \
library"
HOMEPAGE = "https://pypi.org/project/ifaddr/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8debe8d42320ec0ff24665319b625a5e"

SRC_URI[sha256sum] = "1f9e8a6ca6f16db5a37d3356f07b6e52344f6f9f7e806d618537731669eb1a94"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-ctypes \
"
