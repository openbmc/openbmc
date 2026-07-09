DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "abbd0af7a38a8bbb1d6adea2e35b17609cf004eaac323e88a8d8963640dd2b3c"

DEPENDS += "python3-pbr-native"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-pbr python3-six"

BBCLASSEXTEND = "native"
