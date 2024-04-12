SUMMARY = "Provides the core functionality for pydantic validation and serialization."
DESCRIPTION = "This package provides the core functionality for \
pydantic validation and serialization.\
\
Pydantic-core is currently around 17x faster than pydantic V1."
HOMEPAGE = "https://github.com/pydantic/pydantic-core"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab599c188b4a314d2856b3a55030c75c"

SRC_URI += "file://0001-Bumps-pyo3-https-github.com-pyo3-pyo3-from-0.20.2-to.patch"
SRC_URI[sha256sum] = "1cac689f80a3abab2d3c0048b29eea5751114054f032a941a32de4c852c59cad"

DEPENDS = "python3-maturin-native python3-typing-extensions"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_maturin

PYPI_PACKAGE = "pydantic_core"

RDEPENDS:${PN} += "python3-typing-extensions"

INSANE_SKIP:${PN} = "already-stripped"

inherit ptest
SRC_URI += "file://run-ptest"
RDEPENDS:${PN}-ptest += "\
    python3-dirty-equals \
    python3-hypothesis \
    python3-pytest \
    python3-pytest-mock \
    python3-pytest-timeout \
    python3-pytest-benchmark \
    python3-unittest-automake-output \
"

do_install:append() {
    for f in ${D}/${libdir}/${PYTHON_DIR}/site-packages/pydantic_core/_pydantic_core.*.so
    do
        fname=`basename $f`
        lname=`echo $fname | sed 's/musl/gnu/'`
        if [ "$fname" != "$lname" ]; then
            mv $f ${D}/${libdir}/${PYTHON_DIR}/site-packages/pydantic_core/$lname
        fi
    done
}

do_install_ptest() {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    sed -i -e "/--automake/ s/$/ -k 'not test_model_class_root_validator_wrap and not test_model_class_root_validator_before and not test_model_class_root_validator_after'/" ${D}${PTEST_PATH}/run-ptest
}
