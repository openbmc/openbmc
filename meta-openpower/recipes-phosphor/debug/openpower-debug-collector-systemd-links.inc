pkg_postinst:${PN}:append() {
	mkdir -p $D$systemd_system_unitdir/obmc-host-crash@0.target.wants
	mkdir -p $D$systemd_system_unitdir/obmc-host-timeout@0.target.wants

	# The obmc-host-crash target is repurposed for the memory preserving reboot
	# feature when it is enabled. If not enabled, then install the standard
	# debug collector checkstop service for basic error reporting
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'mpreboot', d)}" != mpreboot ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/openpower-debug-collector-checkstop@0.service"
		TARGET="../openpower-debug-collector-checkstop@.service"
		ln -s $TARGET $LINK
	fi

	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog@0.service"
	TARGET="../openpower-debug-collector-watchdog@.service"
	ln -s $TARGET $LINK

	if [ "${@bb.utils.filter('MACHINE_FEATURES', 'phal', d)}" != 'phal' ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog-timeout@0.service"
		TARGET="../openpower-debug-collector-watchdog-timeout@.service"
		ln -s $TARGET $LINK
	fi
}

pkg_postinst:${PN}:append:p10bmc() {
        mkdir -p $D$systemd_system_unitdir/obmc-host-stop@0.target.wants

        LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/clear_hostdumps_poweroff.service"
        TARGET="../clear_hostdumps_poweroff.service"
        ln -s $TARGET $LINK
}

pkg_prerm:${PN}:append() {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'mpreboot', d)}" != mpreboot ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/openpower-debug-collector-checkstop@0.service"
		rm $LINK
	fi
	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog@0.service"
	rm $LINK
	if [ "${@bb.utils.filter('MACHINE_FEATURES', 'phal', d)}" != 'phal' ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog-timeout@0.service"
		rm $LINK
	fi
}

pkg_prerm:${PN}:append:p10bmc() {
        LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/clear_hostdumps_poweroff.service"
        rm $LINK
}
