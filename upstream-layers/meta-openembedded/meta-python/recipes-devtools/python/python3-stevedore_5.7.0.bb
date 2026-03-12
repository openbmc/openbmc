DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "31dd6fe6b3cbe921e21dcefabc9a5f1cf848cf538a1f27543721b8ca09948aa3"

DEPENDS += "python3-pbr-native"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-pbr python3-six"

BBCLASSEXTEND = "native"
