DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c80be45b33471b4a23cf53d06a8172be"

# The PyPI package omits some files for testing like tests/__init__.py
# so use the GitHub source instead.
SRCREV = "ea119aab729d4c48f797f3663a692680c93fb9f5"
SRC_URI = " \
    git://github.com/slashmili/jdatetime;branch=main;protocol=https \
    file://run-ptest \
"

inherit python_setuptools_build_meta ptest

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    python3-modules \
    python3-jalali-core \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-pickle \
    python3-unittest \
    python3-zoneinfo \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
