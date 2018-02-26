do_rootfs[depends] += "prelink-native:do_populate_sysroot"

IMAGE_PREPROCESS_COMMAND_append_libc-glibc = " prelink_setup; prelink_image; "

python prelink_setup () {
    oe.utils.write_ld_so_conf(d)
}

inherit linuxloader

prelink_image () {
#	export PSEUDO_DEBUG=4
#	/bin/env | /bin/grep PSEUDO
#	echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
#	echo "LD_PRELOAD=$LD_PRELOAD"

	pre_prelink_size=`du -ks ${IMAGE_ROOTFS} | awk '{size = $1 ; print size }'`
	echo "Size before prelinking $pre_prelink_size."

	# We need a prelink conf on the filesystem, add one if it's missing
	if [ ! -e ${IMAGE_ROOTFS}${sysconfdir}/prelink.conf ]; then
		cp ${STAGING_ETCDIR_NATIVE}/prelink.conf \
			${IMAGE_ROOTFS}${sysconfdir}/prelink.conf
		dummy_prelink_conf=true;
	else
		dummy_prelink_conf=false;
	fi

	# We need a ld.so.conf with pathnames in,prelink conf on the filesystem, add one if it's missing
	ldsoconf=${IMAGE_ROOTFS}${sysconfdir}/ld.so.conf
	if [ -e $ldsoconf ]; then
		cp $ldsoconf $ldsoconf.prelink
	fi
	cat ${STAGING_DIR_TARGET}${sysconfdir}/ld.so.conf >> $ldsoconf

	dynamic_loader=$(linuxloader)

	# prelink!
	if [ "$BUILD_REPRODUCIBLE_BINARIES" = "1" ]; then
		bbnote " prelink: BUILD_REPRODUCIBLE_BINARIES..."
		if [ "$REPRODUCIBLE_TIMESTAMP_ROOTFS" = "" ]; then
			export PRELINK_TIMESTAMP=`git log -1 --pretty=%ct `
		else
			export PRELINK_TIMESTAMP=$REPRODUCIBLE_TIMESTAMP_ROOTFS
		fi
		${STAGING_SBINDIR_NATIVE}/prelink --root ${IMAGE_ROOTFS} -am -N -c ${sysconfdir}/prelink.conf --dynamic-linker $dynamic_loader
	else
		${STAGING_SBINDIR_NATIVE}/prelink --root ${IMAGE_ROOTFS} -amR -N -c ${sysconfdir}/prelink.conf --dynamic-linker $dynamic_loader
	fi

	# Remove the prelink.conf if we had to add it.
	if [ "$dummy_prelink_conf" = "true" ]; then
		rm -f ${IMAGE_ROOTFS}${sysconfdir}/prelink.conf
	fi

	if [ -e $ldsoconf.prelink ]; then
		mv $ldsoconf.prelink $ldsoconf
	else
		rm $ldsoconf
	fi

	pre_prelink_size=`du -ks ${IMAGE_ROOTFS} | awk '{size = $1 ; print size }'`
	echo "Size after prelinking $pre_prelink_size."
}
