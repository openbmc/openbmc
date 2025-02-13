SUMMARY = "An implementation-agnostic implementation of JSON reference resolution."
HOMEPAGE = "https://github.com/python-jsonschema/referencing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "df2e89862cd09deabbdba16944cc3f10feb6b3e6f18e902f7cc25609a34775aa"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += "python3-rpds-py"

BBCLASSEXTEND = "native nativesdk"
