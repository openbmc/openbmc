SUMMARY = "The JSON Schema meta-schemas and vocabularies, exposed as a Registry"
DESCRIPTION = "JSON support files from the JSON Schema Specifications (metaschemas, \
vocabularies, etc.), packaged for runtime access from Python as a referencing-based Schema Registry."
HOMEPAGE = "https://pypi.org/project/jsonschema-specifications/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=93eb9740964b59e9ba30281255b044e2"

SRC_URI[sha256sum] = "0f38b83639958ce1152d02a7f062902c41c8fd20d558b0c34344292d417ae272"

inherit pypi python_hatchling

PYPI_PACKAGE = "jsonschema_specifications"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

DEPENDS += "python3-hatch-vcs-native"

BBCLASSEXTEND = "native nativesdk"
