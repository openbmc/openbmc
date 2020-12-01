DESCRIPTION = "A robust and significantly extended implementation of JSONPath for Python"
HOMEPAGE = "https://github.com/kennknowles/python-jsonpath-rw"
SECTION = "devel/python"
LICENSE = "BSD+"
LIC_FILES_CHKSUM = "file://README.rst;md5=02384665f821c394981e0dd1faec9a7d"

SRC_URI[md5sum] = "3a807e05c2c12158fc6bb0a402fd5778"
SRC_URI[sha256sum] = "05c471281c45ae113f6103d1268ec7a4831a2e96aa80de45edc89b11fac4fbec"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-decorator"
