SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "https://github.com/python-cmd2/cmd2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4c527bcb481233ebcb803de975f42701"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI[sha256sum] = "25dbb2e9847aaa686a8a21e84e3d101db8b79f5cb992e044fc54210ab8c0ad41"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-colorama \
    ${PYTHON_PN}-pyperclip \
    ${PYTHON_PN}-wcwidth \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-pydoc \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"
