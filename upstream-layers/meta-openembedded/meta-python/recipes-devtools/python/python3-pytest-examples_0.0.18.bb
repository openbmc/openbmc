SUMMARY = "Pytest plugin for testing examples in docstrings and markdown files."
DESCRIPTION = "Pytest plugin for testing examples in docstrings and markdown files."
HOMEPAGE = "https://github.com/pydantic/pytest-examples"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4914bbb433a7975b6b5c5f1bb48bb9d0"

RDEPENDS:${PN} += "\
    python3-pytest \
    python3-core \
    python3-crypt \
    python3-io \
    python3-black \
    python3-ruff \
"

inherit pypi python_hatchling

PYPI_PACKAGE = "pytest_examples"

SRC_URI[sha256sum] = "9a464f007f805b113677a15e2f8942ebb92d7d3eb5312e9a405d018478ec9801"

BBCLASSEXTEND = "native nativesdk"
