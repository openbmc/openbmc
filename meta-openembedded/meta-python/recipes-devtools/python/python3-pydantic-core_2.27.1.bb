SUMMARY = "Provides the core functionality for pydantic validation and serialization."
DESCRIPTION = "This package provides the core functionality for \
pydantic validation and serialization.\
\
Pydantic-core is currently around 17x faster than pydantic V1."
HOMEPAGE = "https://github.com/pydantic/pydantic-core"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab599c188b4a314d2856b3a55030c75c"

require ${BPN}-crates.inc

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "62a763352879b84aa31058fc931884055fd75089cccbd9d58bb6afd01141b235"

DEPENDS = "python3-maturin-native python3-typing-extensions"

inherit pypi cargo-update-recipe-crates python_maturin

PYPI_PACKAGE = "pydantic_core"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-typing-extensions \
"

INSANE_SKIP:${PN} = "already-stripped"
INSANE_SKIP:${PN} += "buildpaths"

# python3-misc is for Lib/timeit.py which is not split out elsewhere
inherit ptest
RDEPENDS:${PN}-ptest += "\
	python3-dateutil \
    python3-dirty-equals \
    python3-hypothesis \
    python3-inline-snapshot \
    python3-misc \
    python3-pytest \
    python3-pytest-mock \
    python3-pytest-timeout \
    python3-pytest-benchmark \
	python3-tzdata \
    python3-unittest-automake-output \
	python3-zoneinfo \
"

do_install_ptest() {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    sed -i -e "/--automake/ s/$/ -k 'not test_model_class_root_validator_wrap and not test_model_class_root_validator_before and not test_model_class_root_validator_after'/" ${D}${PTEST_PATH}/run-ptest
}
