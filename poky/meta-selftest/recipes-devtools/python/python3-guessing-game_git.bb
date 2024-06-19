SUMMARY = "The guessing game from The Rust Book using pyo3."
DESCRIPTION = "Wrap a version of the guessing game from The Rust Book \
to run in Python using pyo3."
HOMEPAGE = "https://www.maturin.rs/tutorial"
SECTION = "devel/python"
LICENSE = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-APACHE;md5=1836efb2eb779966696f473ee8540542 \
                    file://LICENSE-MIT;md5=85fd3b67069cff784d98ebfc7d5c0797"

SRC_URI = "git://git.yoctoproject.org/guessing-game.git;protocol=https;branch=main"

PV = "0.1.0"
SRCREV = "469c9e2230ca4fa9e391c94be6e697733e769500"

S = "${WORKDIR}/git"

inherit python_maturin cargo-update-recipe-crates

require ${BPN}-crates.inc
