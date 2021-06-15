SUMMARY = "OpenPOWER processor control services installation"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

RDEPENDS_${PN} += "op-proc-control"
RDEPENDS_${PN} += "phosphor-state-manager-obmc-targets"

ALLOW_EMPTY_${PN} = "1"

pkg_postinst_${PN}() {
	mkdir -p $D$systemd_system_unitdir/obmc-host-stop@0.target.wants
	mkdir -p $D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires
	mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@0.target.requires
	mkdir -p $D$systemd_system_unitdir/obmc-host-diagnostic-mode@0.target.requires
	mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@0.target.requires
	mkdir -p $D$systemd_system_unitdir/obmc-host-quiesce@0.target.wants

	LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/op-stop-instructions@0.service"
	TARGET="../op-stop-instructions@.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-host-quiesce@0.target.wants/op-stop-instructions@0.service"
	TARGET="../op-stop-instructions@.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/op-cfam-reset.service"
	TARGET="../op-cfam-reset.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.requires/op-cfam-reset.service"
	TARGET="../op-cfam-reset.service"
	ln -s $TARGET $LINK

	# Only install cfam override if p9 system
	if [ "${@bb.utils.contains("MACHINE_FEATURES", "p9-cfam-override", "True", "False", d)}" = True ]; then
		mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@0.target.requires
		LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/cfam_override@0.service"
		TARGET="../cfam_override@.service"
		ln -s $TARGET $LINK
	fi

	LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/op-continue-mpreboot@0.service"
	TARGET="../op-continue-mpreboot@.service"
	ln -s $TARGET $LINK

	# Only install certain units if phal enabled
	if [ "${@bb.utils.filter('OBMC_MACHINE_FEATURES', 'phal', d)}" = phal ]; then
		mkdir -p $D$systemd_system_unitdir/obmc-host-start@0.target.requires
		LINK="$D$systemd_system_unitdir/obmc-host-start@0.target.requires/phal-reinit-devtree.service"
		TARGET="../phal-reinit-devtree.service"
		ln -s $TARGET $LINK

		LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.requires/proc-pre-poweroff@0.service"
		TARGET="../proc-pre-poweroff@.service"
		ln -s $TARGET $LINK
    fi

	# If the memory preserving reboot feature is enabled, set it up
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'mpreboot', d)}" = mpreboot ]; then
		# on mpreboot systems, obmc-host-crash@.target is used for mpreboot
		mkdir -p $D$systemd_system_unitdir/obmc-host-crash@0.target.requires
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.requires/obmc-host-force-warm-reboot@0.target"
		TARGET="../obmc-host-force-warm-reboot@.target"
		ln -s $TARGET $LINK

		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.requires/op-enter-mpreboot@0.service"
		TARGET="../op-enter-mpreboot@.service"
		ln -s $TARGET $LINK

		# ensure diagnostic mode is shown for MPREBOOT
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.requires/obmc-host-diagnostic-mode@0.target"
		TARGET="../obmc-host-diagnostic-mode@.target"
		ln -s $TARGET $LINK
	else
		# If not a mpreboot system, default to quiesce target in crash target to support
		# older system designs like witherspoon
		mkdir -p $D$systemd_system_unitdir/obmc-host-crash@0.target.wants
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/obmc-host-quiesce@0.target"
		TARGET="../obmc-host-quiesce@.target"
		ln -s $TARGET $LINK
	fi
}

pkg_prerm_${PN}() {
	LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/op-stop-instructions@0.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/op-cfam-reset.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.requires/op-cfam-reset.service"
	rm $LINK
	# Only uninstall cfam override if p9 system
	if [ "${@bb.utils.contains("MACHINE_FEATURES", "p9-cfam-override", "True", "False", d)}" = True ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/cfam_override@0.service"
		rm $LINK
	fi
	LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.requires/op-continue-mpreboot@0.service"
	rm $LINK

	# Remove phal specific units if enabled
	if [ "${@bb.utils.filter('OBMC_MACHINE_FEATURES', 'phal', d)}" = phal ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-start@0.target.requires/phal-reinit-devtree.service"
		rm $LINK

		LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.requires/proc-pre-poweroff@0.service"
		rm $LINK
	fi

	# Remove mpreboot specific units if enabled
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'mpreboot', d)}" = mpreboot ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.requires/obmc-host-force-warm-reboot@0.target"
		rm $LINK

		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.requires/op-enter-mpreboot@0.service"
		rm $LINK
	else
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/obmc-host-quiesce@0.target"
		rm $LINK
	fi
}
