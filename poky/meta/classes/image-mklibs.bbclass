do_rootfs[depends] += "mklibs-native:do_populate_sysroot"

IMAGE_PREPROCESS_COMMAND += "mklibs_optimize_image; "

inherit linuxloader

mklibs_optimize_image_doit() {
	rm -rf ${WORKDIR}/mklibs
	mkdir -p ${WORKDIR}/mklibs/dest
	cd ${IMAGE_ROOTFS}
	du -bs > ${WORKDIR}/mklibs/du.before.mklibs.txt

	# Build a list of dynamically linked executable ELF files.
	# Omit libc/libpthread as a special case because it has an interpreter
	# but is primarily what we intend to strip down.
	for i in `find . -type f -executable ! -name 'libc-*' ! -name 'libpthread-*'`; do
		file $i | grep -q ELF || continue
		${HOST_PREFIX}readelf -l $i | grep -q INTERP || continue
		echo $i
	done > ${WORKDIR}/mklibs/executables.list

	dynamic_loader=${@get_linuxloader(d)}

	mklibs -v \
		--ldlib ${dynamic_loader} \
		--libdir ${baselib} \
		--sysroot ${PKG_CONFIG_SYSROOT_DIR} \
		--gcc-options "--sysroot=${PKG_CONFIG_SYSROOT_DIR}" \
		--root ${IMAGE_ROOTFS} \
		--target `echo ${TARGET_PREFIX} | sed 's/-$//' ` \
		-d ${WORKDIR}/mklibs/dest \
		`cat ${WORKDIR}/mklibs/executables.list`

	cd ${WORKDIR}/mklibs/dest
	for i in *
	do
		cp $i `find ${IMAGE_ROOTFS} -name $i`
	done

	cd ${IMAGE_ROOTFS}
	du -bs > ${WORKDIR}/mklibs/du.after.mklibs.txt

	echo rootfs size before mklibs optimization: `cat ${WORKDIR}/mklibs/du.before.mklibs.txt`
	echo rootfs size after mklibs optimization: `cat ${WORKDIR}/mklibs/du.after.mklibs.txt`
}

mklibs_optimize_image() {
	for img in ${MKLIBS_OPTIMIZED_IMAGES}
	do
		if [ "${img}" = "${PN}" ] || [ "${img}" = "all" ]
		then
			mklibs_optimize_image_doit
			break
		fi
	done
}
