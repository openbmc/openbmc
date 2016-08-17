do_install_append() {
	if echo "${DISTRO_FEATURES}" | grep -q 'xen'; then
		echo "xenfs                /proc/xen            xenfs      defaults              0  0" >> ${D}${sysconfdir}/fstab
	fi
}
