SUMMARY = "cargo applet to build and install C-ABI compatible dynamic and static libraries."
HOMEPAGE = "https://crates.io/crates/cargo-c"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=384ed0e2e0b2dac094e51fbf93fdcbe0 \
"


SRC_URI = " \
	git://github.com/lu-zero/cargo-c.git;branch=master;protocol=https \
	file://0001-Add-Cargo.lock-file.patch \
"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

SRCREV = "4eaf39ebbbc9ab8f092adf487d5b53435511d619"
S = "${WORKDIR}/git"

inherit cargo cargo-update-recipe-crates pkgconfig native

DEPENDS = "openssl curl"

require ${BPN}-crates.inc

