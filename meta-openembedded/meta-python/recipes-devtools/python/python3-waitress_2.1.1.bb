SUMMARY = "A WSGI server for Python"
DESCRIPTION = "Waitress is meant to be a production-quality pure-Python WSGI \
    server with very acceptable performance."
HOMEPAGE = "https://github.com/Pylons/waitress"
SECTION = "devel/python"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

RDEPENDS:${PN} += " \
        python3-logging \
"

SRC_URI[sha256sum] = "e2e60576cf14a1539da79f7b7ee1e79a71e64f366a0b47db54a15e971f57bb16"

inherit python_setuptools_build_meta pypi
