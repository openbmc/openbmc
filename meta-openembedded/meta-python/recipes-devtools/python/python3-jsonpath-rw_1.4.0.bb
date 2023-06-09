DESCRIPTION = "A robust and significantly extended implementation of JSONPath for Python"
HOMEPAGE = "https://github.com/kennknowles/python-jsonpath-rw"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;beginline=198;endline=215;md5=2866908485c18dc999b6c8dc608563ec"

SRC_URI[sha256sum] = "05c471281c45ae113f6103d1268ec7a4831a2e96aa80de45edc89b11fac4fbec"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-decorator \
    python3-logging \
    python3-ply \
    python3-six \
"
