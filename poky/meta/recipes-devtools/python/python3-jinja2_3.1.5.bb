SUMMARY = "Python Jinja2: A small but fast and easy to use stand-alone template engine written in pure python."
HOMEPAGE = "https://pypi.org/project/Jinja2/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dc88300786f1c214c1e9827a5229462"

SRC_URI[sha256sum] = "8fefff8dc3034e27bb80d67c671eb8a9bc424c0ef4c0826edbff304cceff43bb"

PYPI_PACKAGE = "jinja2"

CVE_PRODUCT = "jinja2 jinja"

CLEANBROKEN = "1"

inherit pypi python_flit_core ptest-python-pytest

do_install_ptest:append() {
    # test_async items require trio module
    rm -f ${D}${PTEST_PATH}/tests/test_async.py ${D}${PTEST_PATH}/tests/test_async_filters.py
}

RDEPENDS:${PN}-ptest += " \
    python3-unixadmin \
"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-crypt \
    python3-io \
    python3-json \
    python3-markupsafe \
    python3-math \
    python3-netclient \
    python3-numbers\
    python3-pickle \
    python3-pprint \
    python3-shell \
    python3-threading \
"

BBCLASSEXTEND = "native nativesdk"
