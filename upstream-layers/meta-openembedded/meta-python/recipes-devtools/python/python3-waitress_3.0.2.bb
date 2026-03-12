SUMMARY = "A WSGI server for Python"
DESCRIPTION = "Waitress is meant to be a production-quality pure-Python WSGI \
    server with very acceptable performance."
HOMEPAGE = "https://github.com/Pylons/waitress"
SECTION = "devel/python"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

CVE_PRODUCT = "waitress"

RDEPENDS:${PN} += " \
        python3-logging \
"

RDEPENDS:${PN}-ptest += "\
    python3-doctest \
    python3-json \
    python3-multiprocessing \
    python3-pytest \
    python3-unittest \
    python3-unittest-automake-output \
"

SRC_URI += "file://run-ptest"
SRC_URI[sha256sum] = "682aaaf2af0c44ada4abfb70ded36393f0e307f4ab9456a215ce0020baefc31f"

inherit python_setuptools_build_meta pypi ptest

do_install_ptest(){
    cp -r ${S}/tests ${D}${PTEST_PATH}
}
