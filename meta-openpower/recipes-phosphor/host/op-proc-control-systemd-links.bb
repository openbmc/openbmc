SUMMARY = "OpenPOWER processor control services installation"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

RDEPENDS_${PN} += "op-proc-control"
RDEPENDS_${PN} += "phosphor-state-manager-obmc-targets"

ALLOW_EMPTY_${PN} = "1"

pkg_postinst_${PN}() {
	mkdir -p $D$systemd_system_unitdir/obmc-host-stop@0.target.requires
	mkdir -p $D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires

	LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.requires/op-stop-instructions@0.service"
	TARGET="../op-stop-instructions@.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/op-cfam-reset.service"
	TARGET="../op-cfam-reset.service"
	ln -s $TARGET $LINK

	# Only install cfam override if p9 system
	if [ "${@bb.utils.contains("MACHINE_FEATURES", "p9-cfam-override", "True", "False", d)}" = True ]; then
		mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@0.target.requires
		LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/cfam_override@0.service"
		TARGET="../cfam_override@.service"
		ln -s $TARGET $LINK
	fi
}

pkg_prerm_${PN}() {
	LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.requires/op-stop-instructions@0.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/op-cfam-reset.service"
	rm $LINK
	# Only uninstall cfam override if p9 system
	if [ "${@bb.utils.contains("MACHINE_FEATURES", "p9-cfam-override", "True", "False", d)}" = True ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/cfam_override@0.service"
		rm $LINK
	fi
}
