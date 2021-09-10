touch_var_lib_systemd_clock() {
	install -d ${IMAGE_ROOTFS}/var/lib/systemd/timesync/
	touch ${IMAGE_ROOTFS}/var/lib/systemd/timesync/clock
}

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("DISTRO_FEATURES", "systemd", "touch_var_lib_systemd_clock; ", "", d)}'

