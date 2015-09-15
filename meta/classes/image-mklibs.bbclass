do_rootfs[depends] += "mklibs-native:do_populate_sysroot"

IMAGE_PREPROCESS_COMMAND += "mklibs_optimize_image; "

mklibs_optimize_image_doit() {
	rm -rf ${WORKDIR}/mklibs
	mkdir -p ${WORKDIR}/mklibs/dest
	cd ${IMAGE_ROOTFS}
	du -bs > ${WORKDIR}/mklibs/du.before.mklibs.txt
	for i in `find .`; do file $i; done \
		| grep ELF \
		| grep "LSB *executable" \
		| grep "dynamically linked" \
		| sed "s/:.*//" \
		| sed "s+^\./++" \
		> ${WORKDIR}/mklibs/executables.list

	case ${TARGET_ARCH} in
		powerpc | mips | mipsel | microblaze )
			dynamic_loader="${base_libdir}/ld.so.1"
			;;
		powerpc64)
			dynamic_loader="${base_libdir}/ld64.so.1"
			;;
		x86_64)
			dynamic_loader="${base_libdir}/ld-linux-x86-64.so.2"
			;;
		i586 )
			dynamic_loader="${base_libdir}/ld-linux.so.2"
			;;
		arm )
			dynamic_loader="${base_libdir}/ld-linux.so.3"
			;;
		* )
			dynamic_loader="/unknown_dynamic_linker"
			;;
	esac

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
