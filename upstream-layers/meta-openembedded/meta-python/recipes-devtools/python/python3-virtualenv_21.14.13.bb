SUMMARY = "A tool for creating isolated virtual python environments."
DESCRIPTION = "Virtual Python Environment builder"
HOMEPAGE = "https://github.com/pypa/virtualenv"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36ef9437b761b314fa717c786c5ca460"

SRC_URI[sha256sum] = "de5307ba4b9a0001d9c8ea02832d38f63ca432880f416c1bface61c4323fd905"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
    python3-compression  \
    python3-distlib \
    python3-filelock \
    python3-json \
    python3-misc \
    python3-modules \
    python3-platformdirs \
"

CVE_PRODUCT = "virtualenv"
