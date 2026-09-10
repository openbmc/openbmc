DESCRIPTION = "An extremely fast Python linter and code formatter, written in Rust."
SUMMARY = "An extremely fast Python linter and code formatter"
HOMEPAGE = "https://docs.astral.sh/ruff/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e679ca4f742cbfa29ab6a499529c2d39"

DEPENDS += "python3-maturin-native"

SRC_URI[sha256sum] = "13171aa9d9af2240ee3504e639de73122c67e74036de5ba2e1d01422cd17e3dc"

inherit pypi python_setuptools3_rust cargo-update-recipe-crates

CARGO_SRC_DIR = "rust"

require ${BPN}-crates.inc

INSANE_SKIP:${PN} = "already-stripped"

BBCLASSEXTEND = "native nativesdk"
