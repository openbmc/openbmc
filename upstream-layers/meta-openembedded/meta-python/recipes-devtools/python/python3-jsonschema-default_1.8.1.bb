DESCRIPTION = "A Python package that creates default objects from a JSON schema."
HOMEPAGE = "https://github.com/mnboos/jsonschema-default"
LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ae09d45eac4aa08d013b5f2e01c67f6"

SRC_URI[sha256sum] = "77e35e4a95519c7dbdbe25438c00af33c17bb766cbb0a29a2aa9d645ee3c1399"

PYPI_PACKAGE = "jsonschema_default"

inherit pypi python_hatchling

RDEPENDS:${PN} = "python3-rstr"

BBCLASSEXTEND = "native nativesdk"
