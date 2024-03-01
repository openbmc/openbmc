SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "8cbd76cd18c4f870ff39b24284d3ea028fbe8731a58df3aa581e434c575b9559"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	python3-pprint \
"

BBCLASSEXTEND = "native nativesdk"
