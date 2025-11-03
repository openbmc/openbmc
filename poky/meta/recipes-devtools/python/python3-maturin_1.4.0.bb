SUMMARY = "Build and publish crates with pyo3, rust-cpython, cffi bindings and rust binaries as python packages"
HOMEPAGE = "https://github.com/pyo3/maturin"
SECTION = "devel/python"
LICENSE = "MIT | Apache-2.0"
LIC_FILES_CHKSUM = "file://license-apache;md5=1836efb2eb779966696f473ee8540542 \
                    file://license-mit;md5=85fd3b67069cff784d98ebfc7d5c0797"

SRC_URI += "file://0001-Add-32-bit-RISC-V-support.patch"
SRC_URI[sha256sum] = "ed12e1768094a7adeafc3a74ebdb8dc2201fa64c4e7e31f14cfc70378bf93790"
SRC_URI:append = "\
    file://0001-Extract-extension-architecture-name-resolvation-code.patch \
    file://0002-Fix-cross-compilation-issue-with-linux-armv7l-archit.patch \
    file://0003-Extract-extension-ABI-name-resolvation-code-as-helpe.patch \
    file://0004-Fix-cross-compilation-issue-with-linux-ppc-architect.patch \
    file://0005-Fix-cross-compilation-issue-with-linux-mips64-archit.patch \
"

S = "${WORKDIR}/maturin-${PV}"

CFLAGS:append = " -fdebug-prefix-map=${CARGO_HOME}=${TARGET_DBGSRC_DIR}/cargo_home"

DEPENDS += "\
    python3-setuptools-rust-native \
    python3-semantic-version-native \
    python3-setuptools-rust \
"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_pyo3 python_setuptools_build_meta

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
