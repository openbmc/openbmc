SUMMARY = "Phosphor certificate manager configuration for an nslcd authority service"

PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/LICENSE;md5=19407077e42b1ba3d653da313f1f5b4e"

RDEPENDS_${PN} = "phosphor-certificate-manager"

inherit allarch

SRC_URI = "file://env"

do_install() {
	install -D ${WORKDIR}/env ${D}/${sysconfdir}/default/obmc/cert/authority
}

pkg_postinst_${PN}() {
	LINK="$D$systemd_system_unitdir/multi-user.target.wants/phosphor-certificate-manager@authority.service"
	TARGET="../phosphor-certificate-manager@.service"
	mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
	ln -s $TARGET $LINK
}

pkg_prerm_${PN}() {
	LINK="$D$systemd_system_unitdir/multi-user.target.wants/phosphor-certificate-manager@authority.service"
	rm $LINK
}
