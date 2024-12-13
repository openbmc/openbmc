SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "06b6eb0293e4b85e8d385397c5868926820db32b9b654b932f57fa41c23c9978"

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
