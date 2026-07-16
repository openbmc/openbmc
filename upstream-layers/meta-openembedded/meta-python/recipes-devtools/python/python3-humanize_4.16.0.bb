SUMMARY = "Python humanize utilities"
HOMEPAGE = "https://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "7dc2244a2f84a4bfb1d36c37bac80cd78e35cdc5c119206d87b018e1445f3a3f"

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
