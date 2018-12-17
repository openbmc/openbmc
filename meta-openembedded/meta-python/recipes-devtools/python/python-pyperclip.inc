DESCRIPTION = "A cross-platform clipboard module for Python. (only handles plain text for now)"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=4a3129a2c1b8bdc0e2895a1ec0e2dae2"

SRC_URI[md5sum] = "3680607974f99d6e4411b102358b1884"
SRC_URI[sha256sum] = "43496f0a1f363a5ecfc4cda5eba6a2a3d5056fe6c7ffb9a99fbb1c5a3c7dea05"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
