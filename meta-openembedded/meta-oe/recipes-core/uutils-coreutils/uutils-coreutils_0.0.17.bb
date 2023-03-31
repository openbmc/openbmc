SUMMARY = "coreutils ~ GNU coreutils (updated); implemented as universal (cross-platform) utils, written in Rust"
HOMEPAGE = "https://github.com/uutils/coreutils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41f7469eaacac62c67d5664fff2c062d"

inherit cargo cargo-update-recipe-crates

SRC_URI += "git://github.com/uutils/coreutils.git;protocol=https;nobranch=1"
SRCREV = "7e127005afbd6c3632d74ad8082340ccb8329d67"
S = "${WORKDIR}/git"

require ${BPN}-crates.inc

include uutils-coreutils.inc
