SUMMARY = "An implementation-agnostic implementation of JSON reference resolution."
HOMEPAGE = "https://github.com/python-jsonschema/referencing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "44aefc3142c5b842538163acb373e24cce6632bd54bdb01b21ad5863489f50d8"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += "python3-rpds-py"

BBCLASSEXTEND = "native nativesdk"
