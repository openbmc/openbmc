inherit cargo

SRC_URI = "git://github.com/meta-rust/rust-hello-world.git;protocol=https;branch=master"
SRCREV="e0fa23f1a3cb1eb1407165bd2fc36d2f6e6ad728"
LIC_FILES_CHKSUM="file://COPYRIGHT;md5=e6b2207ac3740d2d01141c49208c2147"

SRC_URI += "\
    file://0001-enable-LTO.patch \
    "

UPSTREAM_CHECK_COMMITS = "1"

SUMMARY = "Hello World by Cargo for Rust"
HOMEPAGE = "https://github.com/meta-rust/rust-hello-world"
LICENSE = "MIT | Apache-2.0"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
