SUMMARY="Pylint is a Python source code analyzer"
HOMEPAGE= "http://www.pylint.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c107cf754550e65755c42985a5d4e9c9"

SRC_URI += "git://github.com/pylint-dev/pylint;branch=maintenance/3.0.x;protocol=https \
           file://run-ptest \
           "
SRCREV = "1a5ffc1f447b77071ffe18a9c6836c09147ee2ed"

inherit python_setuptools_build_meta ptest

RDEPENDS:${PN} += "\
    python3-astroid \
    python3-difflib \
    python3-dill \
    python3-isort \
    python3-json \
    python3-mccabe \
    python3-netserver \
    python3-numbers \
    python3-pkgutil \
    python3-platformdirs \
    python3-shell \
    python3-tomlkit \
    "

RDEPENDS:${PN}-ptest += " \
    python3-core \
    python3-git \
    python3-py \
    python3-pytest \
    python3-pytest-benchmark \
    python3-pytest-runner \
    python3-pytest-timeout \
    python3-pytest-xdist \
    python3-requests \
    python3-statistics \
    python3-tomllib \
    python3-typing-extensions \
    python3-unittest-automake-output \
    "

S = "${WORKDIR}/git"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    install -Dm 0644 ${S}/tests/.pylint_primer_tests/.gitkeep ${D}${PTEST_PATH}/tests/.pylint_primer_tests/.gitkeep
    sed -i 's#/usr/bin/python$#/usr/bin/python3#g' ${D}${PTEST_PATH}/tests/data/ascript
    # regression_distutil_import_error_73.py fails to run see
    # https://lists.openembedded.org/g/openembedded-devel/topic/103181847
    rm ${D}${PTEST_PATH}/tests/functional/r/regression_02/regression_distutil_import_error_73.py
}

BBCLASSEXTEND = "native nativesdk"
