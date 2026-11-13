DESCRIPTION = "An extremely fast Python linter and code formatter, written in Rust."
SUMMARY = "An extremely fast Python linter and code formatter"
HOMEPAGE = "https://docs.astral.sh/ruff/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e679ca4f742cbfa29ab6a499529c2d39"

DEPENDS += "python3-maturin-native"

SRC_URI[sha256sum] = "9247bf92b5f04d825c8639a4fe423ec2e4222acd9222e58412b0dab7e442798b"

inherit pypi python_setuptools3_rust cargo-update-recipe-crates

CARGO_SRC_DIR = "rust"

require ${BPN}-crates.inc

INSANE_SKIP:${PN} = "already-stripped"

BBCLASSEXTEND = "native nativesdk"
