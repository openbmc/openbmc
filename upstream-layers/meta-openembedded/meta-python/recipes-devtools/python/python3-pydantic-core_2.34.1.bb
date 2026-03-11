SUMMARY = "Provides the core functionality for pydantic validation and serialization."
DESCRIPTION = "This package provides the core functionality for \
pydantic validation and serialization.\
\
Pydantic-core is currently around 17x faster than pydantic V1."
HOMEPAGE = "https://github.com/pydantic/pydantic-core"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab599c188b4a314d2856b3a55030c75c"

require ${BPN}-crates.inc

SRC_URI += "file://0001-Upgrade-radium-to-1.0.patch;patchdir=${UNPACKDIR}/cargo_home/bitbake/bitvec-1.0.1/"
SRC_URI += "file://0001-cargo.toml-Update-bitvec-to-use-radium-1.x.patch"
SRC_URI[sha256sum] = "6bf31628ab6d0e7c7c0372419898c52ef0a447b33ab47c7f62053bd013cc5b09"

DEPENDS = "python3-maturin-native python3-typing-extensions"

inherit pypi cargo-update-recipe-crates python_maturin ptest-python-pytest

PYPI_PACKAGE = "pydantic_core"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-typing-extensions \
"

INSANE_SKIP:${PN} = "already-stripped"

# python3-misc is for Lib/timeit.py which is not split out elsewhere
RDEPENDS:${PN}-ptest += "\
	python3-dateutil \
    python3-dirty-equals \
    python3-hypothesis \
    python3-inline-snapshot \
    python3-misc \
    python3-pytest-mock \
    python3-pytest-timeout \
    python3-pytest-benchmark \
	python3-tzdata \
	python3-zoneinfo \
"

do_install_ptest:append () {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    sed -i -e "/--automake/ s/$/ -k 'not test_model_class_root_validator_wrap and not test_model_class_root_validator_before and not test_model_class_root_validator_after'/" ${D}${PTEST_PATH}/run-ptest
}

BBCLASSEXTEND = "native nativesdk"
