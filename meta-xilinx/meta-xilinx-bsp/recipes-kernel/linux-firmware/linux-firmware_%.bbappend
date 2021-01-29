# TIInit_11.8.32.bts is required for bluetooth support but this particular
# version is not available in the linux-firmware repository.
#
SRC_URI_append_ultra96 = "\
	https://git.ti.com/ti-bt/service-packs/blobs/raw/c290f8af9e388f37e509ecb111a1b64572b7c225/initscripts/TIInit_11.8.32.bts;name=TIInit_11.8.32 \
	"

SRC_URI[TIInit_11.8.32.md5sum] = "b1e142773e8ef0537b93895ebe2fcae3"
SRC_URI[TIInit_11.8.32.sha256sum] = "962322c05857ad6b1fb81467bdfc59c125e04a6a8eaabf7f24b742ddd68c3bfa"

do_install_append_ultra96() {
	cp ${WORKDIR}/TIInit_11.8.32.bts ${D}${nonarch_base_libdir}/firmware/ti-connectivity/
	( cd ${D}${nonarch_base_libdir}/firmware ; ln -sf ti-connectivity/* . )
	rm -f ${D}${nonarch_base_libdir}/firmware/ti-connectivity/TIInit_7*
	rm -f ${D}${nonarch_base_libdir}/firmware/TIInit_7*
}

INSANE_SKIP_${PN} += "installed-vs-shipped"

PACKAGES_remove_ultra96 = "${PN}-wl12xx"

FILES_${PN}-wl18xx_ultra96 = " \
	${nonarch_base_libdir}/firmware/wl18* \
	${nonarch_base_libdir}/firmware/TI* \
	${nonarch_base_libdir}/firmware/ti-connectivity/wl18* \
	${nonarch_base_libdir}/firmware/ti-connectivity/TI* \
	"

PACKAGE_ARCH_ultra96 = "${BOARD_ARCH}"
