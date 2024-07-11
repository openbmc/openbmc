SUMMARY = "An implementation-agnostic implementation of JSON reference resolution."
HOMEPAGE = "https://github.com/python-jsonschema/referencing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "c775fedf74bc0f9189c2a3be1c12fd03e8c23f4d371dce795df44e06c5b412f7"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += "python3-rpds-py"

BBCLASSEXTEND = "native nativesdk"
