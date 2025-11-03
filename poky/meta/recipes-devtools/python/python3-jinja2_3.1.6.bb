SUMMARY = "Python Jinja2: A small but fast and easy to use stand-alone template engine written in pure python."
HOMEPAGE = "https://pypi.org/project/Jinja2/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dc88300786f1c214c1e9827a5229462"

SRC_URI[sha256sum] = "0137fb05990d35f1275a587e9aee6d56da821fc83491a0fb838183be43f66d6d"

PYPI_PACKAGE = "jinja2"

CVE_PRODUCT = "jinja2 jinja"

CLEANBROKEN = "1"

inherit pypi python_flit_core ptest

SRC_URI += " \
	file://run-ptest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/

    # test_async items require trio module
    rm -f ${D}${PTEST_PATH}/tests/test_async.py ${D}${PTEST_PATH}/tests/test_async_filters.py
}

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
    python3-toml \
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
