SUMMARY = "The multidict implementation"
DESCRIPTION = "Multidicts are useful for working with HTTP headers, URL query args etc."
HOMEPAGE = "https://github.com/aio-libs/multidict/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b5e9e27554f71f0f132919037cb967b2"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI[sha256sum] = "e8af3e6f1bdbc31fd762b094133b478b89988eaebc63cbe82b1d9e3bbd15cede"

RDEPENDS:${PN}-ptest += " \
    python3-objgraph \
    python3-psutil \
    python3-pytest-cov \
    python3-pytest-codspeed \
"

do_install_ptest:append () {
    install -d ${D}${PTEST_PATH}/tools/
    install -m 0644 ${S}/tools/release_notes_md.py ${D}${PTEST_PATH}/tools/
    install -m 0644 ${S}/benchmarks/callgrind_driver.py ${D}${PYTHON_SITEPACKAGES_DIR}/
    install -m 0644 ${S}/benchmarks/benchmark.py ${D}${PYTHON_SITEPACKAGES_DIR}/
    install -m 0644 ${S}/benchmarks/callgrind_child.py ${D}${PYTHON_SITEPACKAGES_DIR}/
    install -m 0644 ${S}/benchmarks/istr.py ${D}${PYTHON_SITEPACKAGES_DIR}/
    install -m 0644 ${S}/benchmarks/operations.py ${D}${PYTHON_SITEPACKAGES_DIR}/
    install -m 0644 ${S}/benchmarks/render_tables.py ${D}${PYTHON_SITEPACKAGES_DIR}/
}

FILES:${PN}-ptest += "\
    ${PYTHON_SITEPACKAGES_DIR}/callgrind_driver.py \
    ${PYTHON_SITEPACKAGES_DIR}/benchmark.py \
    ${PYTHON_SITEPACKAGES_DIR}/callgrind_child.py \
    ${PYTHON_SITEPACKAGES_DIR}/istr.py \
    ${PYTHON_SITEPACKAGES_DIR}/operations.py \
    ${PYTHON_SITEPACKAGES_DIR}/render_tables.py \
"

BBCLASSEXTEND = "native nativesdk"
