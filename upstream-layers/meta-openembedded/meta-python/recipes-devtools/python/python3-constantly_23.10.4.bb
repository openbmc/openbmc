DESCRIPTION = "Symbolic constants in Python"
HOMEPAGE = "https://github.com/twisted/constantly"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e393e4ddd223e3a74982efa784f89fd7"

SRC_URI[sha256sum] = "aa92b70a33e2ac0bb33cd745eb61776594dc48764b06c35e0efd050b7f1c7cbd"

inherit pypi python_poetry_core 

DEPENDS += "python3-versioneer-native"

RDEPENDS:${PN} += "python3-json"
