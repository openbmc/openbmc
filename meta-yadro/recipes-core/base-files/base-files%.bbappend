do_install_basefilesissue () {
	if [ "${hostname}" ]; then
		echo ${hostname} > ${D}${sysconfdir}/hostname
	fi

	# Issue files are provided by os-release recipe instead
}
