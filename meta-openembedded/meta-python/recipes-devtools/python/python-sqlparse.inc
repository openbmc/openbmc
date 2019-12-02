DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI[md5sum] = "2ce34181d6b7b234c9f3c0ecd1ffb93e"
SRC_URI[sha256sum] = "7c3dca29c022744e95b547e867cee89f4fce4373f3549ccd8797d8eb52cdb873"

export BUILD_SYS
export HOST_SYS

inherit pypi
