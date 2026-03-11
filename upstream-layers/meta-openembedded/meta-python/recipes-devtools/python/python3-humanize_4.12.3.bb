SUMMARY = "Python humanize utilities"
HOMEPAGE = "https://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "8430be3a615106fdfceb0b2c1b41c4c98c6b0fc5cc59663a5539b111dd325fb0"

inherit pypi python_hatchling

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
