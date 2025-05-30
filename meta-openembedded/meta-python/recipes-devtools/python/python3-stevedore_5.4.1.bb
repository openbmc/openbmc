DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "3135b5ae50fe12816ef291baff420acb727fcd356106e3e9cbfa9e5985cd6f4b"

DEPENDS += "python3-pbr-native"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pbr python3-six"

BBCLASSEXTEND = "native"
