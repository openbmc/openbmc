SUMMARY = "Pythonic Implementation of lyrics.wikia.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=14;endline=14;md5=95d480cd6f8471abaeae21bd0ed277ba"

SRC_URI[sha256sum] = "c5f36e8ef0ed3b487a9242ce34c19f9684e418a5bbffd5d367dc1d1604b4cd0b"

PYPI_PACKAGE = "PyLyrics"
PYPI_PACKAGE_EXT = "zip"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-beautifulsoup4 \
    python3-classes \
    python3-requests \
"
