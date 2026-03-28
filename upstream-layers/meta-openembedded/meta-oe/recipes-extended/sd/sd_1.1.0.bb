SUMMARY = "sd is an intuitive and fast CLI for search and replace"
HOMEPAGE = "https://github.com/chmln/sd"
DESCRIPTION = "sd is a command-line tool written in Rust for simple and \
               fast find and replace. It uses familiar regular expression \
               syntax and also supports string-literal replacements \
               without escaping. With clear syntax and sensible defaults, \
               it offers an easy-to-use alternative to traditional tools like sed."

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=aec69d9265f7a44821317ebe1e576f1b \
"

SRC_URI = "git://github.com/chmln/sd.git;protocol=https;nobranch=1;tag=v${PV}"
SRCREV = "4a7b216552d64134c0fa17a59b9d557d89019f0f"

inherit cargo cargo-update-recipe-crates

# Build only the main binary ("sd"); helper binaries like "xtask"
# are dev-only and not needed, preventing extra build artifacts.
CARGO_BUILD_FLAGS += "--bin sd"

require ${BPN}-crates.inc

INSANE_SKIP:${PN} = "already-stripped"

BBCLASSEXTEND = "native"
