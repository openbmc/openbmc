SUMMARY = "Phosphor certificate manager configuration for an nslcd authority service"

PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} = "phosphor-certificate-manager"

inherit allarch

SRC_URI = "file://env"

FILES:${PN} = "${datadir}"

do_install() {
	install -D ${WORKDIR}/env ${D}/${datadir}/phosphor-certificate-manager/authority
}

pkg_postinst:${PN}() {
	LINK="$D$systemd_system_unitdir/multi-user.target.wants/phosphor-certificate-manager@authority.service"
	TARGET="../phosphor-certificate-manager@.service"
	mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
	ln -s $TARGET $LINK
}

pkg_prerm:${PN}() {
	LINK="$D$systemd_system_unitdir/multi-user.target.wants/phosphor-certificate-manager@authority.service"
	rm $LINK
}
