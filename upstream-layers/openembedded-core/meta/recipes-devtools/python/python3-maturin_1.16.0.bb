SUMMARY = "Build and publish crates with pyo3, rust-cpython, cffi bindings and rust binaries as python packages"
HOMEPAGE = "https://github.com/pyo3/maturin"
LICENSE = "Apache-2.0 OR MIT"
LIC_FILES_CHKSUM = "file://license-apache;md5=1836efb2eb779966696f473ee8540542 \
                    file://license-mit;md5=85fd3b67069cff784d98ebfc7d5c0797"

SRC_URI[sha256sum] = "13e69ca12542999d8c5467131de72a49bb76e4c284b9a45c5783df7c0fcb3a8a"

CFLAGS += "-ffile-prefix-map=${CARGO_HOME}=${TARGET_DBGSRC_DIR}/cargo_home"

DEPENDS += "\
    python3-setuptools-rust-native \
    python3-semantic-version-native \
    python3-setuptools-rust \
    zstd \
"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_pyo3 python_setuptools_build_meta pkgconfig

do_configure() {
    python_pyo3_do_configure
    cargo_common_do_configure
    python_pep517_do_configure
}

RDEPENDS:${PN} += "\
    cargo \
    python3-json \
    rust \
"

RRECOMMENDS:${PN} += "\
    python3-ensurepip \
    python3-pip \
    python3-venv \
"

BBCLASSEXTEND = "native nativesdk"
