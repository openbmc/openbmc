SUMMARY = "A Python Interface To The cURL library"
DESCRIPTION = "\
PycURL is a Python interface to libcurl, the multiprotocol file \
transfer library. Similarly to the urllib Python module, PycURL can \
be used to fetch objects identified by a URL from a Python program \
"
SECTION = "devel/python"
HOMEPAGE = "http://pycurl.io/"

LICENSE = "LGPL-2.1-only | MIT"
LIC_FILES_CHKSUM = "file://COPYING-LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING-MIT;md5=be42e1b1e58c8d59c2901fd747bfc55d \
                    "

SRC_URI[sha256sum] = "5e3cf357939da8d4ceefe3c7f305afcf9b47cba66cfd95e7768ca43b38445e14"

SRC_URI += " file://run-ptest"

DEPENDS = "curl"

inherit pypi python_setuptools_build_meta ptest

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-flask \
    python3-numpy \
    python3-flaky \
    python3-setuptools \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp -f ${S}/setup.py ${D}${PTEST_PATH}
}

BBCLASSEXTEND  += "native nativesdk"
