SUMMARY = "Python library for CycloneDX"
HOMEPAGE = "https://github.com/CycloneDX/cyclonedx-python-lib/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "16767c4039de90c04e9f03348f8f0ed4b8ff842eaa7eefcad3a95685f970dacf"

inherit pypi python_poetry_core

PYPI_PACKAGE = "cyclonedx_python_lib"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    python3-py-serializable \
    python3-sortedcontainers \
    python3-jsonschema \
    python3-referencing \
"
