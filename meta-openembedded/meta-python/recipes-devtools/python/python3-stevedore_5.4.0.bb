DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "79e92235ecb828fe952b6b8b0c6c87863248631922c8e8e0fa5b17b232c4514d"

DEPENDS += "python3-pbr-native"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pbr python3-six"

BBCLASSEXTEND = "native"
