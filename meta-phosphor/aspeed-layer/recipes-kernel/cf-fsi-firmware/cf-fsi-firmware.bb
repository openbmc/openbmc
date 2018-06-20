SUMMARY = "Firmware for using the ASPEED ColdFire FSI master"
SECTION = "kenrel"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRCREV = "0573025f0147171297c770f31619bd19afe971cf"
SRC_URI = "git://github.com/ozbenh/cf-fsi.git"

S = "${WORKDIR}/git"

inherit allarch

do_compile() {
	:
}

firmware_dir="${nonarch_base_libdir}/firmware/"

do_install() {
	install -d ${D}${firmware_dir}
	install -m 0644 ${S}/dist-bin/cf-fsi-${MACHINE}.bin ${D}${firmware_dir}
}

FILES_${PN} = "${firmware_dir}"
