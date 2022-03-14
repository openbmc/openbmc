SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "3ba2c25dd03fbf3992353595be18152e2fb6042f47b526ea66cd5838bb9f1fb6"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-pprint \
"

BBCLASSEXTEND = "native nativesdk"
