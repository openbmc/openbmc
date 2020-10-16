INHIBIT_SYSROOT_STRIP = "1"

PACKAGES = "${PN}-dbg catchsegv sln nscd ldconfig ldd tzcode glibc-thread-db ${PN}-pic libcidn libmemusage libnss-db libsegfault ${PN}-pcprofile libsotruss ${PN} ${PN}-utils glibc-extra-nss ${PN}-dev ${PN}-staticdev ${PN}-doc"

# The ld.so in this glibc supports the GNU_HASH
RPROVIDES_${PN} = "eglibc rtld(GNU_HASH)"
RPROVIDES_${PN}-utils = "eglibc-utils"
RPROVIDES_${PN}-mtrace = "eglibc-mtrace libc-mtrace"
RPROVIDES_${PN}-pic = "eglibc-pic"
RPROVIDES_${PN}-dev = "eglibc-dev libc6-dev virtual-libc-dev"
RPROVIDES_${PN}-staticdev = "eglibc-staticdev"
RPROVIDES_${PN}-doc = "eglibc-doc"
RPROVIDES_glibc-extra-nss = "eglibc-extra-nss"
RPROVIDES_glibc-thread-db = "eglibc-thread-db"
RPROVIDES_${PN}-pcprofile = "eglibc-pcprofile"
RPROVIDES_${PN}-dbg = "eglibc-dbg"
libc_baselibs = "${base_libdir}/libc.so.* ${base_libdir}/libc-*.so ${base_libdir}/libm*.so.* ${base_libdir}/libm-*.so ${base_libdir}/libmvec-*.so ${base_libdir}/ld*.so.* ${base_libdir}/ld-*.so ${base_libdir}/libpthread*.so.* ${base_libdir}/libpthread-*.so ${base_libdir}/libresolv*.so.* ${base_libdir}/libresolv-*.so ${base_libdir}/librt*.so.* ${base_libdir}/librt-*.so ${base_libdir}/libutil*.so.* ${base_libdir}/libutil-*.so ${base_libdir}/libnsl*.so.* ${base_libdir}/libnsl-*.so ${base_libdir}/libnss_files*.so.* ${base_libdir}/libnss_files-*.so ${base_libdir}/libnss_compat*.so.* ${base_libdir}/libnss_compat-*.so ${base_libdir}/libnss_dns*.so.* ${base_libdir}/libnss_dns-*.so ${base_libdir}/libdl*.so.* ${base_libdir}/libdl-*.so ${base_libdir}/libanl*.so.* ${base_libdir}/libanl-*.so ${base_libdir}/libBrokenLocale*.so.* ${base_libdir}/libBrokenLocale-*.so"
ARCH_DYNAMIC_LOADER = ""
# The aarch64 ABI says the dynamic linker -must- be
# /lib/ld-linux-aarch64{,_be}.so.1. With usrmerge, that may mean that
# we need to install it in /usr/lib.
ARCH_DYNAMIC_LOADER_aarch64 = "ld-linux-${TARGET_ARCH}.so.1"
libc_baselibs_append = " ${@oe.utils.conditional('ARCH_DYNAMIC_LOADER', '', '', '${root_prefix}/lib/${ARCH_DYNAMIC_LOADER}', d)}"
INSANE_SKIP_${PN}_append_aarch64 = " libdir"

FILES_${PN} = "${libc_baselibs} ${libexecdir}/* ${sysconfdir}/ld.so.conf"
RRECOMMENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'ldconfig', '${MLPREFIX}ldconfig', '', d)}"
FILES_ldconfig = "${base_sbindir}/ldconfig"
FILES_ldd = "${bindir}/ldd"
FILES_libsegfault = "${base_libdir}/libSegFault*"
FILES_libcidn = "${base_libdir}/libcidn-*.so ${base_libdir}/libcidn.so.*"
FILES_libmemusage = "${base_libdir}/libmemusage.so"
FILES_libnss-db = "${base_libdir}/libnss_db.so.* ${base_libdir}/libnss_db-*.so ${localstatedir}/db/Makefile ${localstatedir}/db/makedbs.sh"
RDEPENDS_libnss-db = "${PN}-utils"
FILES_glibc-extra-nss = "${base_libdir}/libnss_*-*.so ${base_libdir}/libnss_*.so.*"
FILES_sln = "${base_sbindir}/sln"
FILES_${PN}-pic = "${libdir}/*_pic.a ${libdir}/*_pic.map ${libdir}/libc_pic/*.o"
FILES_libsotruss = "${libdir}/audit/sotruss-lib.so"
FILES_SOLIBSDEV = "${libdir}/lib*${SOLIBSDEV}"
FILES_${PN}-dev += "${libdir}/*_nonshared.a ${base_libdir}/*_nonshared.a ${base_libdir}/*.o ${datadir}/aclocal"
RDEPENDS_${PN}-dev = "linux-libc-headers-dev"
FILES_${PN}-staticdev += "${libdir}/*.a ${base_libdir}/*.a"
FILES_nscd = "${sbindir}/nscd* ${sysconfdir}/init.d/nscd ${systemd_unitdir}/system/nscd* ${sysconfdir}/tmpfiles.d/nscd.conf \
              ${sysconfdir}/nscd.conf ${sysconfdir}/default/volatiles/98_nscd ${localstatedir}/db/nscd"
FILES_${PN}-mtrace = "${bindir}/mtrace"
FILES_tzcode = "${bindir}/tzselect ${sbindir}/zic ${sbindir}/zdump"
FILES_${PN}-utils = "${bindir}/* ${sbindir}/*"
FILES_catchsegv = "${bindir}/catchsegv"
RDEPENDS_catchsegv = "libsegfault"
FILES_${PN}-pcprofile = "${base_libdir}/libpcprofile.so"
FILES_glibc-thread-db = "${base_libdir}/libthread_db.so.* ${base_libdir}/libthread_db-*.so"
RPROVIDES_${PN}-dev += "libc-dev"
RPROVIDES_${PN}-staticdev += "libc-staticdev"

SUMMARY_sln = "The static ln"
DESCRIPTION_sln = "Similar to the 'ln' utility, but statically linked.  sln is useful to make symbolic links to dynamic libraries if the dynamic linking system, for some reason, is not functional."
SUMMARY_nscd = "Name service cache daemon"
DESCRIPTION_nscd = "nscd, name service cache daemon, caches name service lookups for the passwd, group and hosts information.  It can damatically improvide performance with remote, such as NIS or NIS+, name services."
SUMMARY_glibc-extra-nss = "hesiod, NIS and NIS+ nss libraries"
DESCRIPTION_glibc-extra-nss = "glibc: nis, nisplus and hesiod search services."
SUMMARY_ldd = "print shared library dependencies"
DESCRIPTION_ldd = "${bindir}/ldd prints shared library dependencies for each program or shared library specified on the command line."
SUMMARY_${PN}-utils = "Miscellaneous utilities provided by glibc"
DESCRIPTION_${PN}-utils = "Miscellaneous utilities including getconf, iconv, locale, gencat, ..."
DESCRIPTION_libsotruss = "Library to support sotruss which traces calls through PLTs"
DESCRIPTION_tzcode = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"

inherit multilib_header

do_install() {
	oe_runmake install_root=${D} install
	install -Dm 0644 ${WORKDIR}/etc/ld.so.conf ${D}/${sysconfdir}/ld.so.conf
	install -d ${D}${localedir}
	make -f ${WORKDIR}/generate-supported.mk IN="${S}/localedata/SUPPORTED" OUT="${WORKDIR}/SUPPORTED"
	# get rid of some broken files...
	for i in ${GLIBC_BROKEN_LOCALES}; do
		sed -i "/$i/d" ${WORKDIR}/SUPPORTED
	done
	rm -f ${D}${sysconfdir}/rpc
	rm -rf ${D}${datadir}/zoneinfo
	rm -rf ${D}${libexecdir}/getconf

	rm -f ${D}${sysconfdir}/localtime

	# remove empty glibc dir
	if [ -d ${D}${libexecdir} ]; then
		rmdir --ignore-fail-on-non-empty ${D}${libexecdir}
	fi

	oe_multilib_header bits/syscall.h bits/long-double.h bits/floatn.h bits/endianness.h bits/struct_rwlock.h

	if [ -f ${D}${bindir}/mtrace ]; then
		sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' -e '2s,exec.*perl,exec ${USRBINPATH}/env perl,' ${D}${bindir}/mtrace
	fi
	# Info dir listing isn't interesting at this point so remove it if it exists.
	if [ -e "${D}${infodir}/dir" ]; then
		rm -f ${D}${infodir}/dir
	fi

	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${localstatedir}/db/nscd
	install -m 0755 ${S}/nscd/nscd.init ${D}${sysconfdir}/init.d/nscd
	install -m 0755 ${S}/nscd/nscd.conf ${D}${sysconfdir}/nscd.conf
	install -m 0755 ${WORKDIR}/makedbs.sh ${D}${localstatedir}/db
	sed -i "s%daemon%start-stop-daemon --start --exec%g" ${D}${sysconfdir}/init.d/nscd
	sed -i "s|\(enable-cache\t\+netgroup\t\+\)yes|\1no|" ${D}${sysconfdir}/nscd.conf

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/nscd/nscd.service ${D}${systemd_unitdir}/system/

	# The dynamic loader will have been installed into
	# ${base_libdir}. However, if that isn't going to end up being
	# available in the ABI-mandated location, then a symlink must
	# be created.

	if [ -n "${ARCH_DYNAMIC_LOADER}" -a ! -e "${D}${root_prefix}/lib/${ARCH_DYNAMIC_LOADER}" ]; then
		install -d ${D}${root_prefix}/lib
		ln -s ${@oe.path.relative('${root_prefix}/lib', '${base_libdir}')}/${ARCH_DYNAMIC_LOADER} \
				${D}${root_prefix}/lib/${ARCH_DYNAMIC_LOADER}
	fi
}

def get_libc_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU') in [ 'soft', 'ppc-efd' ]:
        return "--without-fp"
    return ""

do_install_append_class-target() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/tmpfiles.d
		echo "d /run/nscd 755 root root -" \
			> ${D}${sysconfdir}/tmpfiles.d/nscd.conf
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/default/volatiles
		echo "d root root 0755 /var/run/nscd none" \
			> ${D}${sysconfdir}/default/volatiles/98_nscd
	fi

}
do_install_append_aarch64 () {
	do_install_armmultilib
}

do_install_append_arm () {
	do_install_armmultilib
}

do_install_append_armeb () {
	do_install_armmultilib
}

do_install_armmultilib () {
	oe_multilib_header bits/endian.h bits/fcntl.h bits/fenv.h bits/fp-fast.h bits/hwcap.h bits/ipc.h bits/link.h
	oe_multilib_header bits/local_lim.h bits/mman.h bits/msq.h bits/pthreadtypes.h bits/pthreadtypes-arch.h  bits/sem.h  bits/semaphore.h bits/setjmp.h
	oe_multilib_header bits/shm.h bits/sigstack.h bits/stat.h bits/statfs.h bits/typesizes.h
	oe_multilib_header bits/procfs-id.h bits/procfs.h bits/shmlba.h

	oe_multilib_header fpu_control.h gnu/lib-names.h gnu/stubs.h ieee754.h

	oe_multilib_header sys/elf.h sys/procfs.h sys/ptrace.h sys/ucontext.h sys/user.h
}


LOCALESTASH = "${WORKDIR}/stashed-locale"
bashscripts = "mtrace sotruss xtrace"

do_stash_locale () {
	dest=${LOCALESTASH}
	install -d $dest${base_libdir} $dest${bindir} $dest${libdir} $dest${datadir}
	# Hide away the locale data from the deployment
	if [ -e ${D}${bindir}/localedef ]; then
		cp -a ${D}${bindir}/localedef $dest${bindir}
	fi
	if [ -e ${D}${libdir}/gconv ]; then
		cp -a ${D}${libdir}/gconv $dest${libdir}
	fi
	if [ -e ${D}${datadir}/i18n ]; then
		cp -a  ${D}${datadir}/i18n $dest${datadir}
	fi

	# Make a copy of all the libraries into the locale stash
	cp -fpPR ${D}${libdir}/* $dest${libdir}
	if [ "${base_libdir}" != "${libdir}" ]; then
		cp -fpPR ${D}${base_libdir}/* $dest${base_libdir}
	fi
	if [ -e ${D}${exec_prefix}/lib ]; then
		if [ ${exec_prefix}/lib != ${base_libdir} ] && [ ${exec_prefix}/lib != ${libdir} ]; then
			cp -fpPR ${D}${exec_prefix}/lib $dest${exec_prefix}
		fi
	fi

	cp -fpPR ${D}${datadir}/* $dest${datadir}
	cp -fpPR ${WORKDIR}/SUPPORTED $dest

	target=$dest/scripts
	mkdir -p $target
	for i in ${bashscripts}; do
		if [ -f ${D}${bindir}/$i ]; then
			cp ${D}${bindir}/$i $target/
		fi
	done
}

addtask do_stash_locale after do_install before do_populate_sysroot do_package
do_stash_locale[dirs] = "${B}"
do_stash_locale[cleandirs] = "${LOCALESTASH}"
SSTATETASKS += "do_stash_locale"
do_stash_locale[sstate-inputdirs] = "${LOCALESTASH}"
do_stash_locale[sstate-outputdirs] = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale"
do_stash_locale[sstate-fixmedir] = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale"

python do_stash_locale_setscene () {
    sstate_setscene(d)
}
addtask do_stash_locale_setscene

PACKAGE_PREPROCESS_FUNCS += "stash_locale_package_cleanup"
SYSROOT_PREPROCESS_FUNCS += "stash_locale_sysroot_cleanup"
stash_locale_cleanup () {
	cleanupdir=$1
	# Remove all files which do_stash_locale() copies
	for i in ${bashscripts}; do
		rm -f $cleanupdir${bindir}/$i
	done
	rm -f $cleanupdir${bindir}/localedef
	rm -rf $cleanupdir${datadir}/i18n
	rm -rf $cleanupdir${libdir}/gconv
	rm -rf $cleanupdir${localedir}
	rm -rf $cleanupdir${datadir}/locale
	rmdir --ignore-fail-on-non-empty $cleanupdir${datadir}

	if [ "${libdir}" != "${exec_prefix}/lib" ] && [ "${root_prefix}/lib" != "${exec_prefix}/lib" ]; then
		if [ -d "$cleanupdir${exec_prefix}/lib" ]; then
			if [ -z "${ARCH_DYNAMIC_LOADER}" -o \
			     ! -e "$cleanupdir${exec_prefix}/lib/${ARCH_DYNAMIC_LOADER}" ]; then
				# error out if directory isn't empty
				# this dir should only contain locale dir
				# which has been deleted in the previous step
				rmdir $cleanupdir${exec_prefix}/lib
			fi
		fi
	fi
}

stash_locale_sysroot_cleanup() {
	stash_locale_cleanup ${SYSROOT_DESTDIR}
}
stash_locale_package_cleanup() {
	stash_locale_cleanup ${PKGD}
}

python populate_packages_prepend () {
    if d.getVar('DEBIAN_NAMES'):
        pkgs = d.getVar('PACKAGES').split()
        bpn = d.getVar('BPN')
        prefix = d.getVar('MLPREFIX') or ""
        # Set the base package...
        d.setVar('PKG_' + prefix + bpn, prefix + 'libc6')
        libcprefix = prefix + bpn + '-'
        for p in pkgs:
            # And all the subpackages.
            if p.startswith(libcprefix):
                renamed = p.replace(bpn, 'libc6', 1)
                d.setVar('PKG_' + p, renamed)
        # For backward compatibility with old -dbg package
        d.appendVar('RPROVIDES_' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
        d.appendVar('RCONFLICTS_' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
        d.appendVar('RREPLACES_' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
}

pkg_postinst_nscd () {
	if [ -z "$D" ]; then
		if command -v systemd-tmpfiles >/dev/null; then
			systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/nscd.conf
		elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
			${sysconfdir}/init.d/populate-volatile.sh update
		fi
	fi
}
CONFFILES_nscd="${sysconfdir}/nscd.conf"

SYSTEMD_PACKAGES = "nscd"
SYSTEMD_SERVICE_nscd = "nscd.service"
