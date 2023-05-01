SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "3e531c91860a81f885f7e6e97d219ae9772cb899580084788935dad7d9742ef0"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-pprint \
"

BBCLASSEXTEND = "native nativesdk"
