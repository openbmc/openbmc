DESCRIPTION = "A comprehensive, fast, pure Python memcached client"
HOMEPAGE = "https://github.com/pinterest/pymemcache"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

PYPI_PACKAGE = "pymemcache"

SRC_URI[sha256sum] = "27bf9bd1bbc1e20f83633208620d56de50f14185055e49504f4f5e94e94aff94"

inherit pypi setuptools3

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-six-native \
"

RDEPENDS:${PN} += "\
    python3-io \
    python3-core \
    python3-logging \
"
