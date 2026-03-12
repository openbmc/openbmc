SUMMARY = "Runtime typing introspection tools"
HOMEPAGE = "https://github.com/pydantic/typing-inspection"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dfe2d84c58973d6a532c4e7638dbb3d8"

DEPENDS = "python3-hatchling-native"

inherit pypi python_hatchling ptest-python-pytest
SRC_URI[sha256sum] = "ba561c48a67c5958007083d386c3295464928b01faa735ab8547c5692e87f464"

RDEPENDS:${PN}-ptest += "python3-typing-extensions"

PYPI_PACKAGE = "typing_inspection"

BBCLASSEXTEND += "native nativesdk"
