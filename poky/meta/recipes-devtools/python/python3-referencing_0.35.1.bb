SUMMARY = "An implementation-agnostic implementation of JSON reference resolution."
HOMEPAGE = "https://github.com/python-jsonschema/referencing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "25b42124a6c8b632a425174f24087783efb348a6f1e0008e63cd4466fedf703c"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += "python3-rpds-py"

BBCLASSEXTEND = "native nativesdk"
