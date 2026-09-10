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
SRC_URI[ripgrep.sha256sum] = "a30750b6d0743bfdd2656ebbaf4555aa278c43144b84bc389bcbfa399485ec71"
S = "${CARGO_VENDORING_DIRECTORY}/ripgrep-${PV}"

inherit cargo cargo-update-recipe-crates

DEPENDS:append:class-target = " libstd-rs"

require ${BPN}-crates.inc

BBCLASSEXTEND = "native"
