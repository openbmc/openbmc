SUMMARY = "Python library for CycloneDX"
HOMEPAGE = "https://github.com/CycloneDX/cyclonedx-python-lib/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "4b3194db72b613717f2912447e67ab618c75ff7dcac6c4af3c0e9e1ac617c102"

inherit pypi python_poetry_core

PYPI_PACKAGE = "cyclonedx_python_lib"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    python3-py-serializable \
    python3-sortedcontainers \
    python3-jsonschema \
    python3-referencing \
"
