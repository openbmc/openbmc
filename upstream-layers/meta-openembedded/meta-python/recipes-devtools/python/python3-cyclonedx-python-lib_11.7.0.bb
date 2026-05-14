SUMMARY = "Python library for CycloneDX"
HOMEPAGE = "https://github.com/CycloneDX/cyclonedx-python-lib/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "fb1bc3dedfa31208444dbd743007f478ab6984010a184e5bd466bffd969e936e"

inherit pypi python_poetry_core

PYPI_PACKAGE = "cyclonedx_python_lib"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    python3-py-serializable \
    python3-sortedcontainers \
    python3-jsonschema \
    python3-referencing \
"
