DESCRIPTION = "pytest-asyncio is an Apache2 licensed library, written in Python, for testing asyncio code with pytest"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=136e4f49dbf29942c572a3a8f6e88a77"

SRC_URI[sha256sum] = "d081d828e576d85f875399194281e92bf8a68d60d72d1a2faf2feddb6c46b276"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "pytest_asyncio"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-pytest \
"

BBCLASSEXTEND = "native nativesdk"
