SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "aed2c273547ae72a0d5ee869719c02a643da16bf507c80958faadc7e038e3f73"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-pprint \
"

BBCLASSEXTEND = "native nativesdk"
