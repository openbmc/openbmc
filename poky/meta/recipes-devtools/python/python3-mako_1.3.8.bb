SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d0995d6f7ba3f186a03118f244e88f57"

PYPI_PACKAGE = "mako"

inherit pypi python_setuptools_build_meta ptest-python-pytest



SRC_URI[sha256sum] = "577b97e414580d3e088d47c2dbbe9594aa7a5146ed2875d4dfa9075af2dd3cc8"

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
