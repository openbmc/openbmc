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

SRC_URI[sha256sum] = "682aaaf2af0c44ada4abfb70ded36393f0e307f4ab9456a215ce0020baefc31f"

inherit python_setuptools_build_meta pypi
