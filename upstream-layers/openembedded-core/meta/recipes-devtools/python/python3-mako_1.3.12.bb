SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=73026b50800163bd3c75cfdc121d9eb5"

PYPI_PACKAGE = "mako"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI[sha256sum] = "9f778e93289bd410bb35daadeb4fc66d95a746f0b75777b942088b7fd7af550a"

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
