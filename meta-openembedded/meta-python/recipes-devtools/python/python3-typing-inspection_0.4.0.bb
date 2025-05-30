SUMMARY = "Runtime typing introspection tools"
HOMEPAGE = "https://github.com/pydantic/typing-inspection"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=07cbaa23fc9dd504fc1ea5acc23b0add"

DEPENDS = "python3-hatchling-native"
SRC_URI[sha256sum] = "9765c87de36671694a67904bf2c96e395be9c6439bb6c87b5142569dcdd65122"

inherit pypi python_hatchling ptest-python-pytest

RDEPENDS:${PN}-ptest += "python3-typing-extensions"

PYPI_PACKAGE = "typing_inspection"

BBCLASSEXTEND += "native nativesdk"
