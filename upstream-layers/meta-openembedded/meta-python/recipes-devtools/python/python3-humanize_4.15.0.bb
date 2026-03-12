SUMMARY = "Python humanize utilities"
HOMEPAGE = "https://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "1dd098483eb1c7ee8e32eb2e99ad1910baefa4b75c3aff3a82f4d78688993b10"

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
