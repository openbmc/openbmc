SUMMARY = "ripgrep - Fast, recursive search tool like grep, written in Rust"
HOMEPAGE = "https://crates.io/crates/ripgrep"
DESCRIPTION = "ripgrep recursively searches directories for a regex pattern \
               while respecting .gitignore. It's fast, safe, and written \
               entirely in Rust."

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE-MIT;md5=8d0d0aa488af0ab9aafa3b85a7fc8e12 \
"

SRC_URI = "crate://crates.io/ripgrep/${PV};name=ripgrep"
SRC_URI[ripgrep.sha256sum] = "f388c4955f85477c28a8667355819844a06614b083c23517f0e86bd1d6d82b73"
S = "${CARGO_VENDORING_DIRECTORY}/ripgrep-${PV}"

inherit cargo cargo-update-recipe-crates

DEPENDS:append:class-target = " libstd-rs"

require ${BPN}-crates.inc

BBCLASSEXTEND = "native"
