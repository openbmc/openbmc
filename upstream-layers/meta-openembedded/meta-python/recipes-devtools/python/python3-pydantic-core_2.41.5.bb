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
SRC_URI += "file://atomic.patch;patchdir=${UNPACKDIR}/cargo_home/bitbake/radium-1.1.0/"
SRC_URI += "file://0001-musl-enable-getrandom-on-all-musl-platforms.patch;patchdir=${UNPACKDIR}/cargo_home/bitbake/libc-0.2.155/"
SRC_URI += "file://0001-cargo.toml-Update-bitvec-to-use-radium-1.x.patch"
SRC_URI += "crate://crates.io/ahash/0.8.12 \
           crate://crates.io/aho-corasick/1.1.3 \
           crate://crates.io/autocfg/1.3.0 \
           crate://crates.io/base64/0.22.1 \
           crate://crates.io/bitflags/2.9.1 \
           crate://crates.io/bitvec/1.0.1 \
           crate://crates.io/bumpalo/3.19.0 \
           crate://crates.io/cc/1.0.101 \
           crate://crates.io/cfg-if/1.0.0 \
           crate://crates.io/displaydoc/0.2.5 \
           crate://crates.io/enum_dispatch/0.3.13 \
           crate://crates.io/form_urlencoded/1.2.1 \
           crate://crates.io/funty/2.0.0 \
           crate://crates.io/getrandom/0.3.3 \
           crate://crates.io/heck/0.5.0 \
           crate://crates.io/hex/0.4.3 \
           crate://crates.io/icu_collections/1.5.0 \
           crate://crates.io/icu_locid/1.5.0 \
           crate://crates.io/icu_locid_transform/1.5.0 \
           crate://crates.io/icu_locid_transform_data/1.5.0 \
           crate://crates.io/icu_normalizer/1.5.0 \
           crate://crates.io/icu_normalizer_data/1.5.0 \
           crate://crates.io/icu_properties/1.5.1 \
           crate://crates.io/icu_properties_data/1.5.0 \
           crate://crates.io/icu_provider/1.5.0 \
           crate://crates.io/icu_provider_macros/1.5.0 \
           crate://crates.io/idna/1.1.0 \
           crate://crates.io/idna_adapter/1.2.0 \
           crate://crates.io/indoc/2.0.5 \
           crate://crates.io/itoa/1.0.11 \
           crate://crates.io/jiter/0.11.1 \
           crate://crates.io/js-sys/0.3.77 \
           crate://crates.io/lexical-parse-float/1.0.5 \
           crate://crates.io/lexical-parse-integer/1.0.5 \
           crate://crates.io/lexical-util/1.0.6 \
           crate://crates.io/libc/0.2.155 \
           crate://crates.io/litemap/0.7.3 \
           crate://crates.io/log/0.4.27 \
           crate://crates.io/memchr/2.7.4 \
           crate://crates.io/memoffset/0.9.1 \
           crate://crates.io/num-bigint/0.4.6 \
           crate://crates.io/num-integer/0.1.46 \
           crate://crates.io/num-traits/0.2.19 \
           crate://crates.io/once_cell/1.21.3 \
           crate://crates.io/percent-encoding/2.3.2 \
           crate://crates.io/portable-atomic/1.6.0 \
           crate://crates.io/proc-macro2/1.0.86 \
           crate://crates.io/pyo3/0.26.0 \
           crate://crates.io/pyo3-build-config/0.26.0 \
           crate://crates.io/pyo3-ffi/0.26.0 \
           crate://crates.io/pyo3-macros/0.26.0 \
           crate://crates.io/pyo3-macros-backend/0.26.0 \
           crate://crates.io/python3-dll-a/0.2.14 \
           crate://crates.io/quote/1.0.36 \
           crate://crates.io/r-efi/5.2.0 \
           crate://crates.io/radium/1.1.0 \
           crate://crates.io/regex/1.12.2 \
           crate://crates.io/regex-automata/0.4.13 \
           crate://crates.io/regex-syntax/0.8.5 \
           crate://crates.io/rustversion/1.0.17 \
           crate://crates.io/ryu/1.0.18 \
           crate://crates.io/serde/1.0.228 \
           crate://crates.io/serde_core/1.0.228 \
           crate://crates.io/serde_derive/1.0.228 \
           crate://crates.io/serde_json/1.0.145 \
           crate://crates.io/smallvec/1.15.1 \
           crate://crates.io/speedate/0.17.0 \
           crate://crates.io/stable_deref_trait/1.2.0 \
           crate://crates.io/static_assertions/1.1.0 \
           crate://crates.io/strum/0.27.2 \
           crate://crates.io/strum_macros/0.27.2 \
           crate://crates.io/syn/2.0.82 \
           crate://crates.io/synstructure/0.13.1 \
           crate://crates.io/tap/1.0.1 \
           crate://crates.io/target-lexicon/0.13.2 \
           crate://crates.io/tinystr/0.7.6 \
           crate://crates.io/unicode-ident/1.0.12 \
           crate://crates.io/unindent/0.2.3 \
           crate://crates.io/url/2.5.4 \
           crate://crates.io/utf16_iter/1.0.5 \
           crate://crates.io/utf8_iter/1.0.4 \
           crate://crates.io/uuid/1.18.1 \
           crate://crates.io/version_check/0.9.5 \
           crate://crates.io/wasi/0.14.2+wasi-0.2.4 \
           crate://crates.io/wasm-bindgen/0.2.100 \
           crate://crates.io/wasm-bindgen-backend/0.2.100 \
           crate://crates.io/wasm-bindgen-macro/0.2.100 \
           crate://crates.io/wasm-bindgen-macro-support/0.2.100 \
           crate://crates.io/wasm-bindgen-shared/0.2.100 \
           crate://crates.io/wit-bindgen-rt/0.39.0 \
           crate://crates.io/write16/1.0.0 \
           crate://crates.io/writeable/0.5.5 \
           crate://crates.io/wyz/0.5.1 \
           crate://crates.io/yoke/0.7.4 \
           crate://crates.io/yoke-derive/0.7.4 \
           crate://crates.io/zerocopy/0.8.25 \
           crate://crates.io/zerocopy-derive/0.8.25 \
           crate://crates.io/zerofrom/0.1.4 \
           crate://crates.io/zerofrom-derive/0.1.4 \
           crate://crates.io/zerovec/0.10.4 \
           crate://crates.io/zerovec-derive/0.10.3 \
           "
SRC_URI[sha256sum] = "08daa51ea16ad373ffd5e7606252cc32f07bc72b28284b6bc9c6df804816476e"

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
    python3-typing-inspection \
	python3-tzdata \
	python3-zoneinfo \
"

do_install_ptest:append () {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    sed -i -e "/--automake/ s/$/ -k 'not test_model_class_root_validator_wrap and not test_model_class_root_validator_before and not test_model_class_root_validator_after'/" ${D}${PTEST_PATH}/run-ptest
}

BBCLASSEXTEND = "native nativesdk"
