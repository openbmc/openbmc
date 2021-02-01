pkg_postinst_${PN}_append() {
	mkdir -p $D$systemd_system_unitdir/obmc-host-crash@0.target.wants
	mkdir -p $D$systemd_system_unitdir/obmc-host-timeout@0.target.wants

	LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/openpower-debug-collector-checkstop@0.service"
	TARGET="../openpower-debug-collector-checkstop@.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog@0.service"
	TARGET="../openpower-debug-collector-watchdog@.service"
	ln -s $TARGET $LINK

	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog-timeout@0.service"
	TARGET="../openpower-debug-collector-watchdog-timeout.service"
	ln -s $TARGET $LINK
}

pkg_prerm_${PN}_append() {
	LINK="$D$systemd_system_unitdir/obmc-host-crash@0.target.wants/openpower-debug-collector-checkstop@0.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog@0.service"
	rm $LINK
	LINK="$D$systemd_system_unitdir/obmc-host-timeout@0.target.wants/openpower-debug-collector-watchdog-timeout@0.service"
	rm $LINK
}
