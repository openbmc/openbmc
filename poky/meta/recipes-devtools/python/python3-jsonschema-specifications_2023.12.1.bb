SUMMARY = "The JSON Schema meta-schemas and vocabularies, exposed as a Registry"
DESCRIPTION = "JSON support files from the JSON Schema Specifications (metaschemas, \
vocabularies, etc.), packaged for runtime access from Python as a referencing-based Schema Registry."
HOMEPAGE = "https://pypi.org/project/jsonschema-specifications/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "48a76787b3e70f5ed53f1160d2b81f586e4ca6d1548c5de7085d1682674764cc"

inherit pypi python_hatchling

PYPI_PACKAGE = "jsonschema_specifications"

DEPENDS += "python3-hatch-vcs-native"

BBCLASSEXTEND = "native nativesdk"
