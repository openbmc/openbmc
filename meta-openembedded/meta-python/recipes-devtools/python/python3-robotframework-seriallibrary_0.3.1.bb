SUMMARY = "Robot Framework test library for serial connection"
HOMEPAGE = "https://github.com/whosaysni/robotframework-seriallibrary"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI[md5sum] = "b7c9565d54c30df7cd3f3c0e29adffa3"
SRC_URI[sha256sum] = "256ad60fc0b7df4be44d82c302f5ed8fad4935cda99e4b45942e3c88179d1e19"

PYPI_PACKAGE = "robotframework-seriallibrary"

inherit pypi setuptools3

SRC_URI += "file://e31d5fdf2ea00ac6349e64580a20816783064dd4.patch"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pyserial \
    ${PYTHON_PN}-robotframework \
"

BBCLASSEXTEND = "native nativesdk"
