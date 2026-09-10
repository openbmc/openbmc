DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "e97a2667923efda926e8713fde6a73616df68210a3cbc6f02b48967b676fd8bf"

DEPENDS += "python3-pbr-native"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native"
