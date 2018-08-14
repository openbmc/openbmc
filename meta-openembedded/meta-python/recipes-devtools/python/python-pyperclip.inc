DESCRIPTION = "A cross-platform clipboard module for Python. (only handles plain text for now)"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=1ed92115c8f96b19ec34ed09df586e51"

SRC_URI[md5sum] = "d2f6a3129cd6f2518b52bb0b8deeeda7"
SRC_URI[sha256sum] = "ce829433a9af640e08ee89b20f7c62132714bcc5d77df114044d0fccb8c3b3b8"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
"
