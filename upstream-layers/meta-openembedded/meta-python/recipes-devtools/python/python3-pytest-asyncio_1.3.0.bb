DESCRIPTION = "pytest-asyncio is an Apache2 licensed library, written in Python, for testing asyncio code with pytest"
HOMEPAGE = "https://github.com/pytest-dev/pytest-asyncio"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=136e4f49dbf29942c572a3a8f6e88a77"

SRC_URI[sha256sum] = "d7f52f36d231b80ee124cd216ffb19369aa168fc10095013c6b014a34d3ee9e5"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "pytest_asyncio"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-pytest \
"

BBCLASSEXTEND = "native nativesdk"
