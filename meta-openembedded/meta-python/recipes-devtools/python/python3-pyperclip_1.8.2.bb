DESCRIPTION = "A cross-platform clipboard module for Python. (only handles plain text for now)"
HOMEPAGE = "https://github.com/asweigart/pyperclip"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d7dd4b0d1f7153322a546e89b5a0a632"

SRC_URI[sha256sum] = "105254a8b04934f0bc84e9c24eb360a591aaf6535c9def5f29d92af107a9bf57"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
