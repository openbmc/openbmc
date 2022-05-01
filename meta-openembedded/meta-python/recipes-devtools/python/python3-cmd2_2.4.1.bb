SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "https://github.com/python-cmd2/cmd2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=209e288518b0668115f58c3929af9ff1"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI[sha256sum] = "f3b0467daca18fca0dc7838de7726a72ab64127a018a377a86a6ed8ebfdbb25f"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-colorama \
    ${PYTHON_PN}-pyperclip \
    ${PYTHON_PN}-wcwidth \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-pydoc \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
