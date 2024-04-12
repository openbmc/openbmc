SUMMARY = "An implementation-agnostic implementation of JSON reference resolution."
HOMEPAGE = "https://github.com/python-jsonschema/referencing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "5773bd84ef41799a5a8ca72dc34590c041eb01bf9aa02632b4a973fb0181a844"

inherit pypi python_hatchling

DEPENDS += "${PYTHON_PN}-hatch-vcs-native"

RDEPENDS:${PN} += "python3-rpds-py"

BBCLASSEXTEND = "native nativesdk"
