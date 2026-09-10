SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c79ceff89df0a72f29bb0e1b6f0e36ed"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI[sha256sum] = "d7904710b662996425a21627710c4777c45053146942cf8a7aebf757c92b8c27"

CVE_PRODUCT = "makotemplates:mako sqlalchemy:mako"

RDEPENDS:${PN} = "python3-html \
                  python3-markupsafe \
                  python3-misc \
                  python3-netclient \
                  python3-pygments \
                  python3-threading \
"

PTEST_PYTEST_DIR = "test"

do_install_ptest:append() {
    install -m 0644 ${S}/setup.cfg ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
