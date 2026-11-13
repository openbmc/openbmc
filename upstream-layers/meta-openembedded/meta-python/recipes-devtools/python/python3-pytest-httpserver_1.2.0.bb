SUMMARY = "HTTP server for pytest to test http clients"
DESCRIPTION = "HTTP server for pytest to test http clients"
HOMEPAGE = "https://github.com/csernazs/pytest-httpserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f54416ccc5478427995b27e5ef38e92"

SRC_URI[sha256sum] = "d07744ce2efaafa0575ed3081d416b999c773b09e84aa95065138bedc3e12352"

inherit pypi python_poetry_core ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
        python3-requests \
"

RDEPENDS:${PN} += " \
        python3-werkzeug \
"
