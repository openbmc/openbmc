pkg_postinst_${PN}_append() {
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

	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog-timeout@0.service"
	TARGET="../openpower-debug-collector-watchdog-timeout.service"
	ln -s $TARGET $LINK
}

pkg_prerm_${PN}_append() {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'mpreboot', d)}" != mpreboot ]; then
		LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/openpower-debug-collector-checkstop@0.service"
		rm $LINK
	fi
	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog@0.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog-timeout@0.service"
	rm $LINK
}
