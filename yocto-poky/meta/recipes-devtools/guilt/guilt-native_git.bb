SUMMARY = "quilt-like tool for Git"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=b6f3400dc1a01cebafe8a52b3f344135"

inherit native

SRC_URI = "git://repo.or.cz/guilt.git \
           file://guilt-bash.patch \
           "
PV = "0.35+git${SRCPV}"
SRCREV = "c2a5bae511c6d5354aa4e1cb59069c31df2b8eeb"

S = "${WORKDIR}/git"

# we don't compile, we just install
do_compile() {
	:
}

do_install() {
	oe_runmake PREFIX=${D}/${prefix} install
}
