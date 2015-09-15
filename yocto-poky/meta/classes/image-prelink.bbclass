do_rootfs[depends] += "prelink-native:do_populate_sysroot"

IMAGE_PREPROCESS_COMMAND += "prelink_image; "

prelink_image () {
#	export PSEUDO_DEBUG=4
#	/bin/env | /bin/grep PSEUDO
#	echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
#	echo "LD_PRELOAD=$LD_PRELOAD"

	pre_prelink_size=`du -ks ${IMAGE_ROOTFS} | awk '{size = $1 ; print size }'`
	echo "Size before prelinking $pre_prelink_size."

	# We need a prelink conf on the filesystem, add one if it's missing
	if [ ! -e ${IMAGE_ROOTFS}${sysconfdir}/prelink.conf ]; then
		cp ${STAGING_DIR_NATIVE}${sysconfdir_native}/prelink.conf \
			${IMAGE_ROOTFS}${sysconfdir}/prelink.conf
		dummy_prelink_conf=true;
	else
		dummy_prelink_conf=false;
	fi

	# prelink!
	${STAGING_DIR_NATIVE}${sbindir_native}/prelink --root ${IMAGE_ROOTFS} -amR -N -c ${sysconfdir}/prelink.conf

	# Remove the prelink.conf if we had to add it.
	if [ "$dummy_prelink_conf" = "true" ]; then
		rm -f ${IMAGE_ROOTFS}${sysconfdir}/prelink.conf
	fi

	pre_prelink_size=`du -ks ${IMAGE_ROOTFS} | awk '{size = $1 ; print size }'`
	echo "Size after prelinking $pre_prelink_size."
}
