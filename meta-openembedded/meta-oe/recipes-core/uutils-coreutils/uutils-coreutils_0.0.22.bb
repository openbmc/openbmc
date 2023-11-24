SUMMARY = "coreutils ~ GNU coreutils (updated); implemented as universal (cross-platform) utils, written in Rust"
HOMEPAGE = "https://github.com/uutils/coreutils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41f7469eaacac62c67d5664fff2c062d"

inherit cargo cargo-update-recipe-crates

SRC_URI += "git://github.com/uutils/coreutils.git;protocol=https;branch=main"

# musl not supported because the libc crate does not support functions like "endutxent" at the moment,
# so src/uucore/src/lib/features.rs disables utmpx when targetting musl.
COMPATIBLE_HOST:libc-musl = "null"

SRCREV = "73b7c46131a9ce35ca1015e76fea772c40d41dd7"
S = "${WORKDIR}/git"

require ${BPN}-crates.inc

include uutils-coreutils.inc
