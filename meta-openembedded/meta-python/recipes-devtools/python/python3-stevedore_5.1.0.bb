DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "a54534acf9b89bc7ed264807013b505bf07f74dbe4bcfa37d32bd063870b087c"

DEPENDS += "python3-pbr-native"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pbr python3-six"

BBCLASSEXTEND = "native"
