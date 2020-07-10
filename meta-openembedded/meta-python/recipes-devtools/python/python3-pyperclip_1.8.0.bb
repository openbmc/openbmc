DESCRIPTION = "A cross-platform clipboard module for Python. (only handles plain text for now)"
HOMEPAGE = "https://github.com/asweigart/pyperclip"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d7dd4b0d1f7153322a546e89b5a0a632"

SRC_URI[md5sum] = "c2564a74b909b6fc32401afd619de83d"
SRC_URI[sha256sum] = "b75b975160428d84608c26edba2dec146e7799566aea42c1fe1b32e72b6028f2"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
