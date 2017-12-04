# this build system is mostly shared by attr and acl

SRC_URI += "file://relative-libdir.patch;striplevel=0 \
           "

inherit autotools-brokensep gettext

# the package comes with a custom config.h.in, it cannot be
# overwritten by autoheader
EXTRA_AUTORECONF += "--exclude=autoheader"
EXTRA_OECONF = "INSTALL_USER=root INSTALL_GROUP=root"
EXTRA_OECONF_append_class-native = " --enable-gettext=no"
EXTRA_OECONF_append_class-target = "${@['', ' --disable-gettext '][(d.getVar('USE_NLS', True) == 'no')]}"

EXTRA_OEMAKE = "PKG_LIB_DIR=${base_libdir} PKG_DEVLIB_DIR=${libdir}"

do_install () {
	oe_runmake install install-lib install-dev DIST_ROOT="${D}"
}

do_install_append_class-native () {
	if test "${libdir}" = "${base_libdir}" ; then
		return
	fi
	librelpath=${@os.path.relpath(d.getVar('libdir',True), d.getVar('base_libdir', True))}
	baselibrelpath=${@os.path.relpath(d.getVar('base_libdir',True), d.getVar('libdir', True))}

	# Remove bad symlinks & create the correct symlinks
	if test -L ${D}${libdir}/lib${BPN}.so ; then
		rm -rf ${D}${libdir}/lib${BPN}.so
		ln -sf $baselibrelpath/lib${BPN}.so ${D}${libdir}/lib${BPN}.so
	fi
	if test -L ${D}${base_libdir}/lib${BPN}.a ; then
		rm -rf ${D}${base_libdir}/lib${BPN}.a
		ln -sf $librelpath/lib${BPN}.a ${D}${base_libdir}/lib${BPN}.a
	fi
	if test -L  ${D}${base_libdir}/lib${BPN}.la ; then
		rm -rf ${D}${base_libdir}/lib${BPN}.la
		ln -sf $librelpath/lib${BPN}.la ${D}${base_libdir}/lib${BPN}.la
	fi
}

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${base_libdir}/lib*${SOLIBS}"

BBCLASSEXTEND = "native"
# Only append ldflags for target recipe and if USE_NLS is enabled
LDFLAGS_append_libc-uclibc_class-target = "${@['', ' -lintl '][(d.getVar('USE_NLS', True) == 'yes')]}"
EXTRA_OECONF_append_libc-uclibc_class-target = "${@['', ' --disable-gettext '][(d.getVar('USE_NLS', True) == 'no')]}"
