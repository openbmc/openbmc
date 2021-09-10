SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/dreamhost/cliff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "f5bc8ecabc05bb9d291eb5203d6810b49040f6ff446a756326104746cc00c1db"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-prettytable \
    ${PYTHON_PN}-cmd2 \
    ${PYTHON_PN}-pyparsing"

BBCLASSEXTEND = "native nativesdk"
