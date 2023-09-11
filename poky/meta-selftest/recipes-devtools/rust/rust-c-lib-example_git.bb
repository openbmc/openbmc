DESCRIPTION = "A simple example for rust C library"
HOMEPAGE = "https://gitlab.com/fmartinsonsHome/rust-c-lib-example"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb9c441273ed8a029701a086befbfc63"

SRC_URI = " \
	git://gitlab.com/fmartinsonsHome/rust-c-lib-example.git;branch=main;protocol=https \
"

SRCREV = "fc53c457f69aa5221ec1f8619a007e8150db5e60"
S = "${WORKDIR}/git"

inherit cargo_c

require ${BPN}-crates.inc
