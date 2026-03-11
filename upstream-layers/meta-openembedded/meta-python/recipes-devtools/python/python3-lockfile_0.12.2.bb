# This recipe is originally from meta-openstack:
# https://git.yoctoproject.org/cgit/cgit.cgi/meta-cloud-services/tree/meta-openstack/recipes-devtools/python/python-lockfile_0.12.2.bb

SUMMARY = "Platform-independent file locking module"
HOMEPAGE = "https://pypi.org/project/lockfile/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2340dffbbfea534b58f1349984eeef72"

SRC_URI[sha256sum] = "6aed02de03cba24efabcd600b30540140634fc06cfa603822d508d5361e9f799"

inherit pypi setuptools3

DEPENDS += "python3-distutils-extra-native python3-pbr-native"
RDEPENDS:${PN} += " \
    python3-io \
    python3-sqlite3 \
"

BBCLASSEXTEND = "native nativesdk"
