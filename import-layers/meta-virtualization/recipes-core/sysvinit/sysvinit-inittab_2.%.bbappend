do_install_append() {
	if echo "${DISTRO_FEATURES}" | grep -q 'xen'; then
		echo "" >> ${D}${sysconfdir}/inittab
		echo "X0:12345:respawn:/sbin/getty 115200 hvc0" >> ${D}${sysconfdir}/inittab
	fi
}
