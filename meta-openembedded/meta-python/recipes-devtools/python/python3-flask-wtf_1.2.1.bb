DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d98d089889e14b227732d45dac3aacc4"

SRC_URI[sha256sum] = "8bb269eb9bb46b87e7c8233d7e7debdf1f8b74bf90cc1789988c29b37a97b695"

PYPI_PACKAGE = "flask_wtf"
UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/Flask-WTF"
UPSTREAM_CHECK_REGEX = "/Flask-WTF/(?P<pver>(\d+[\.\-_]*)+)"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    python3-flask \
    python3-itsdangerous \
    python3-json \
    python3-wtforms \
"
