SUMMARY = "Phosphor Power Monitor services installation"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

RDEPENDS_${PN}-monitor += "phosphor-power-monitor"

ALLOW_EMPTY_${PN} = "1"


pkg_postinst_${PN}() {
	mkdir -p $D$systemd_system_unitdir/multi-user.target.requires

	[ -z "${OBMC_POWER_SUPPLY_INSTANCES}" ] && echo "No power supply instance defined" && exit 1

	for inst in ${OBMC_POWER_SUPPLY_INSTANCES}; do
		LINK="$D$systemd_system_unitdir/multi-user.target.requires/power-supply-monitor@$inst.service"
		TARGET="../power-supply-monitor@.service"
		ln -s $TARGET $LINK
	done
}

pkg_prerm_${PN}() {
	[ -z "${OBMC_POWER_SUPPLY_INSTANCES}" ] && echo "No power supply instance defined" && exit 1

	for inst in ${OBMC_POWER_SUPPLY_INSTANCES}; do
		LINK="$D$systemd_system_unitdir/multi-user.target.requires/power-supply-monitor@$inst.service"
		rm $LINK
	done
}
