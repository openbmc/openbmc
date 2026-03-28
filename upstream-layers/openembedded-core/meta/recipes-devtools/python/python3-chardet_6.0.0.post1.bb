SUMMARY = "Universal encoding detector for Python 2 and 3"
HOMEPAGE = "https://pypi.org/project/chardet/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

SRC_URI[sha256sum] = "6b78048c3c97c7b2ed1fbad7a18f76f5a6547f7d34dbab536cc13887c9a92fa4"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

PACKAGES =+ "${PN}-cli"
FILES:${PN}-cli += " \
    ${PYTHON_SITEPACKAGES_DIR}/chardet/cli \
"

RDEPENDS:${PN}-cli = "${PN} "

RDEPENDS:${PN}:class-target += " \
    python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
