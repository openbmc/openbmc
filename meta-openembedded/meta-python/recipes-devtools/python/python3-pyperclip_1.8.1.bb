DESCRIPTION = "A cross-platform clipboard module for Python. (only handles plain text for now)"
HOMEPAGE = "https://github.com/asweigart/pyperclip"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d7dd4b0d1f7153322a546e89b5a0a632"

SRC_URI[md5sum] = "0ac879899da5c2af755a834245ca6a0f"
SRC_URI[sha256sum] = "9abef1e79ce635eb62309ecae02dfb5a3eb952fa7d6dce09c1aef063f81424d3"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
