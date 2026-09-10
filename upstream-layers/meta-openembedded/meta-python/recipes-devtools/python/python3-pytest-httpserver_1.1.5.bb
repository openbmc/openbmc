SUMMARY = "HTTP server for pytest to test http clients"
DESCRIPTION = "HTTP server for pytest to test http clients"
HOMEPAGE = "https://github.com/csernazs/pytest-httpserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f54416ccc5478427995b27e5ef38e92"

SRC_URI[sha256sum] = "dc3d82e1fe00e491829d8939c549bf4bd9b39a260f87113c619b9d517c2f8ff1"

PYPI_PACKAGE = "pytest_httpserver"

inherit pypi python_poetry_core ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
        python3-requests \
"

RDEPENDS:${PN} += " \
        python3-werkzeug \
"
