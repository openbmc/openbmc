DESCRIPTION = "Symbolic constants in Python"
HOMEPAGE = "https://github.com/twisted/constantly"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e393e4ddd223e3a74982efa784f89fd7"

SRC_URI[md5sum] = "f0762f083d83039758e53f8cf0086eef"
SRC_URI[sha256sum] = "586372eb92059873e29eba4f9dec8381541b4d3834660707faf8ba59146dfc35"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-json"
