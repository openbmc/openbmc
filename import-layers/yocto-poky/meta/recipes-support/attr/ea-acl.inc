# this build system is mostly shared by attr and acl

SRC_URI += "file://relative-libdir.patch;striplevel=0 \
           "

inherit autotools-brokensep gettext

# the package comes with a custom config.h.in, it cannot be
# overwritten by autoheader
EXTRA_AUTORECONF += "--exclude=autoheader"
EXTRA_OECONF = "INSTALL_USER=root INSTALL_GROUP=root"
EXTRA_OECONF_append_class-native = " --enable-gettext=no"

EXTRA_OEMAKE = "PKG_LIB_DIR=${base_libdir} PKG_DEVLIB_DIR=${libdir}"

do_install () {
	oe_runmake install install-lib install-dev DIST_ROOT="${D}"
}

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${base_libdir}/lib*${SOLIBS}"

BBCLASSEXTEND = "native"
# Only append ldflags for target recipe and if USE_NLS is enabled
LDFLAGS_append_libc-uclibc_class-target = "${@['', ' -lintl '][(d.getVar('USE_NLS', True) == 'yes')]}"
EXTRA_OECONF_append_libc-uclibc_class-target = "${@['', ' --disable-gettext '][(d.getVar('USE_NLS', True) == 'no')]}"

fix_symlink () {
	if [ "${BB_CURRENTTASK}" != "populate_sysroot" -a "${BB_CURRENTTASK}" != "populate_sysroot_setscene" ]
	then
		return
	fi

	if test "${libdir}" = "${base_libdir}" ; then
		return
	fi
	# Remove bad symlinks & create the correct symlinks
	if test -L ${libdir}/lib${BPN}.so ; then
		rm -rf ${libdir}/lib${BPN}.so
		ln -sf ${base_libdir}/lib${BPN}.so ${libdir}/lib${BPN}.so
	fi
	if test -L ${base_libdir}/lib${BPN}.a ; then
		rm -rf ${base_libdir}/lib${BPN}.a
		ln -sf ${libdir}/lib${BPN}.a ${base_libdir}/lib${BPN}.a
	fi
	if test -L  ${base_libdir}/lib${BPN}.la ; then
		rm -rf ${base_libdir}/lib${BPN}.la
		ln -sf ${libdir}/lib${BPN}.la ${base_libdir}/lib${BPN}.la
	fi
}
SSTATEPOSTINSTFUNCS_class-native += "fix_symlink"
