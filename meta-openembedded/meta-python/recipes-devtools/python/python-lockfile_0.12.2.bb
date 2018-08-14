DESCRIPTION = "Platform-independent file locking module"
HOMEPAGE = "http://launchpad.net/pylockfile"
SECTION = "devel/python"

RDEPENDS_${PN} = "python-threading"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit pypi setuptools

SRC_URI[md5sum] = "a6a1a82957a23afdf44cfdd039b65ff9"
SRC_URI[sha256sum] = "6aed02de03cba24efabcd600b30540140634fc06cfa603822d508d5361e9f799"

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

