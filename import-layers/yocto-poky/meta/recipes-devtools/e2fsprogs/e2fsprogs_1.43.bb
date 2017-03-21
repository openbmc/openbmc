require e2fsprogs.inc

PR = "r1"

SRC_URI += "file://acinclude.m4 \
            file://remove.ldconfig.call.patch \
            file://quiet-debugfs.patch \
            file://run-ptest \
            file://ptest.patch \
            file://mkdir.patch \
            file://Revert-mke2fs-enable-the-metadata_csum-and-64bit-fea.patch \
"

SRC_URI_append_class-native = " file://e2fsprogs-fix-missing-check-for-permission-denied.patch"

SRCREV = "d6adf070b0e85f209c0d7f310188b134b5cb7180"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+\.\d+(\.\d+)*)$"

EXTRA_OECONF += "--libdir=${base_libdir} --sbindir=${base_sbindir} \
                --enable-elf-shlibs --disable-libuuid --disable-uuidd \
                --disable-libblkid --enable-verbose-makecmds"

EXTRA_OECONF_darwin = "--libdir=${base_libdir} --sbindir=${base_sbindir} --enable-bsd-shlibs"

PACKAGECONFIG ??= ""
PACKAGECONFIG[fuse] = '--enable-fuse2fs,--disable-fuse2fs,fuse'

do_configure_prepend () {
	cp ${WORKDIR}/acinclude.m4 ${S}/
}

do_install () {
	oe_runmake 'DESTDIR=${D}' install
	oe_runmake 'DESTDIR=${D}' install-libs
	# We use blkid from util-linux now so remove from here
	rm -f ${D}${base_libdir}/libblkid*
	rm -rf ${D}${includedir}/blkid
	rm -f ${D}${base_libdir}/pkgconfig/blkid.pc
	rm -f ${D}${base_sbindir}/blkid
	rm -f ${D}${base_sbindir}/fsck
	rm -f ${D}${base_sbindir}/findfs

	# e2initrd_helper and the pkgconfig files belong in libdir
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		install -d ${D}${libdir}
		mv ${D}${base_libdir}/e2initrd_helper ${D}${libdir}
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
	fi

	oe_multilib_header ext2fs/ext2_types.h
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/chattr ${D}${base_bindir}/chattr.e2fsprogs

	install -v -m 755 ${S}/contrib/populate-extfs.sh ${D}${base_sbindir}/

	# Clean host path (build directory) in compile_et, mk_cmds
	sed -i -e "s,\(ET_DIR=.*\)${S}/lib/et\(.*\),\1${datadir}/et\2,g" ${D}${bindir}/compile_et
	sed -i -e "s,\(SS_DIR=.*\)${S}/lib/ss\(.*\),\1${datadir}/ss\2,g" ${D}${bindir}/mk_cmds
}

# Need to find the right mke2fs.conf file
e2fsprogs_conf_fixup () {
	for i in mke2fs mkfs.ext2 mkfs.ext3 mkfs.ext4 mkfs.ext4dev; do
		create_wrapper ${D}${base_sbindir}/$i MKE2FS_CONFIG=${sysconfdir}/mke2fs.conf
	done
}

do_install_append_class-native() {
	e2fsprogs_conf_fixup
}

do_install_append_class-nativesdk() {
	e2fsprogs_conf_fixup
}

RDEPENDS_e2fsprogs = "e2fsprogs-badblocks"
RRECOMMENDS_e2fsprogs = "e2fsprogs-mke2fs e2fsprogs-e2fsck"

PACKAGES =+ "e2fsprogs-e2fsck e2fsprogs-mke2fs e2fsprogs-tune2fs e2fsprogs-badblocks e2fsprogs-resize2fs"
PACKAGES =+ "libcomerr libss libe2p libext2fs"

FILES_e2fsprogs-resize2fs = "${base_sbindir}/resize2fs*"
FILES_e2fsprogs-e2fsck = "${base_sbindir}/e2fsck ${base_sbindir}/fsck.ext*"
FILES_e2fsprogs-mke2fs = "${base_sbindir}/mke2fs ${base_sbindir}/mkfs.ext* ${sysconfdir}/mke2fs.conf"
FILES_e2fsprogs-tune2fs = "${base_sbindir}/tune2fs ${base_sbindir}/e2label"
FILES_e2fsprogs-badblocks = "${base_sbindir}/badblocks"
FILES_libcomerr = "${base_libdir}/libcom_err.so.*"
FILES_libss = "${base_libdir}/libss.so.*"
FILES_libe2p = "${base_libdir}/libe2p.so.*"
FILES_libext2fs = "${libdir}/e2initrd_helper ${base_libdir}/libext2fs.so.*"
FILES_${PN}-dev += "${datadir}/*/*.awk ${datadir}/*/*.sed ${base_libdir}/*.so ${bindir}/compile_et ${bindir}/mk_cmds"

ALTERNATIVE_${PN} = "chattr"
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_LINK_NAME[chattr] = "${base_bindir}/chattr"
ALTERNATIVE_TARGET[chattr] = "${base_bindir}/chattr.e2fsprogs"

ALTERNATIVE_${PN}-doc = "fsck.8"
ALTERNATIVE_LINK_NAME[fsck.8] = "${mandir}/man8/fsck.8"

RDEPENDS_${PN}-ptest += "${PN} ${PN}-tune2fs coreutils procps bash"

do_compile_ptest() {
	oe_runmake -C ${B}/tests
}

do_install_ptest() {
	cp -a ${B}/tests ${D}${PTEST_PATH}/test
	cp -a ${S}/tests/* ${D}${PTEST_PATH}/test
	sed -e 's!../e2fsck/e2fsck!e2fsck!g' -i ${D}${PTEST_PATH}/test/*/expect*
}
