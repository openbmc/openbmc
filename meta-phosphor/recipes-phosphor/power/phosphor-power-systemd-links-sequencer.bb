SUMMARY = "Phosphor Power Sequencer services installation"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

RDEPENDS_${PN} += "phosphor-power-sequencer"

ALLOW_EMPTY_${PN} = "1"

pkg_postinst_${PN}() {
	mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants
	mkdir -p $D$systemd_system_unitdir/multi-user.target.requires

	LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/pseq-monitor.service"
	TARGET="../pseq-monitor.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/pseq-monitor-pgood.service"
	TARGET="../pseq-monitor-pgood.service"
	ln -s $TARGET $LINK
}

pkg_prerm_${PN}() {
	LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/pseq-monitor.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/pseq-monitor-pgood.service"
	rm $LINK
}
