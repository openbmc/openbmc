SUMMARY = "DTS Coherent Acoustics decoder with support for HD extensions"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c"

SRCREV = "b93deed1a231dd6dd7e39b9fe7d2abe05aa00158"
SRC_URI = "git://github.com/foo86/dcadec.git;protocol=http"

S = "${WORKDIR}/git"

inherit lib_package

EXTRA_OEMAKE = "CONFIG_SHARED=1"

do_install() {
	oe_runmake install DESTDIR="${D}" PREFIX="${prefix}"
}
