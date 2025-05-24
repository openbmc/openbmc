SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=73026b50800163bd3c75cfdc121d9eb5"

PYPI_PACKAGE = "mako"

inherit pypi python_setuptools_build_meta ptest-python-pytest



SRC_URI[sha256sum] = "b5d65ff3462870feec922dbccf38f6efb44e5714d7b593a656be86663d8600ac"

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
