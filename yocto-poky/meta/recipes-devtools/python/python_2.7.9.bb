require python.inc
DEPENDS = "python-native libffi bzip2 db gdbm openssl readline sqlite3 zlib"
PR = "${INC_PR}"

DISTRO_SRC_URI ?= "file://sitecustomize.py"
DISTRO_SRC_URI_linuxstdbase = ""
SRC_URI += "\
  file://01-use-proper-tools-for-cross-build.patch \
  file://03-fix-tkinter-detection.patch \
  file://06-avoid_usr_lib_termcap_path_in_linking.patch \
  ${DISTRO_SRC_URI} \
  file://multilib.patch \
  file://cgi_py.patch \
  file://setup_py_skip_cross_import_check.patch \
  file://add-md5module-support.patch \
  file://host_include_contamination.patch \
  file://fix_for_using_different_libdir.patch \
  file://setuptweaks.patch \
  file://check-if-target-is-64b-not-host.patch \
  file://search_db_h_in_inc_dirs_and_avoid_warning.patch \
  file://avoid_warning_about_tkinter.patch \
  file://avoid_warning_for_sunos_specific_module.patch \
  file://python-2.7.3-remove-bsdb-rpath.patch \
  file://fix-makefile-for-ptest.patch \
  file://run-ptest \
  file://parallel-makeinst-create-bindir.patch \
  file://use_sysroot_ncurses_instead_of_host.patch \
  file://avoid_parallel_make_races_on_pgen.patch \
"

S = "${WORKDIR}/Python-${PV}"

inherit autotools multilib_header python-dir pythonnative

CONFIGUREOPTS += " --with-system-ffi "

# The 3 lines below are copied from the libffi recipe, ctypes ships its own copy of the libffi sources
#Somehow gcc doesn't set __SOFTFP__ when passing -mfloatabi=softp :(
TARGET_CC_ARCH_append_armv6 = " -D__SOFTFP__"
TARGET_CC_ARCH_append_armv7a = " -D__SOFTFP__"

# The following is a hack until we drop ac_cv_sizeof_off_t from site files
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'largefile', 'ac_cv_sizeof_off_t=8', '', d)} ac_cv_file__dev_ptmx=yes ac_cv_file__dev_ptc=no"

do_configure_append() {
	rm -f ${S}/Makefile.orig
        autoreconf -Wcross --verbose --install --force --exclude=autopoint ../Python-${PV}/Modules/_ctypes/libffi
}

do_compile() {
        # regenerate platform specific files, because they depend on system headers
        cd ${S}/Lib/plat-linux2
        include=${STAGING_INCDIR} ${STAGING_BINDIR_NATIVE}/python-native/python \
                ${S}/Tools/scripts/h2py.py -i '(u_long)' \
                ${STAGING_INCDIR}/dlfcn.h \
                ${STAGING_INCDIR}/linux/cdrom.h \
                ${STAGING_INCDIR}/netinet/in.h \
                ${STAGING_INCDIR}/sys/types.h
        sed -e 's,${STAGING_DIR_HOST},,g' -i *.py
        cd -

	# remove any bogus LD_LIBRARY_PATH
	sed -i -e s,RUNSHARED=.*,RUNSHARED=, Makefile

	if [ ! -f Makefile.orig ]; then
		install -m 0644 Makefile Makefile.orig
	fi
	sed -i -e 's#^LDFLAGS=.*#LDFLAGS=${LDFLAGS} -L. -L${STAGING_LIBDIR}#g' \
		-e 's,libdir=${libdir},libdir=${STAGING_LIBDIR},g' \
		-e 's,libexecdir=${libexecdir},libexecdir=${STAGING_DIR_HOST}${libexecdir},g' \
		-e 's,^LIBDIR=.*,LIBDIR=${STAGING_LIBDIR},g' \
		-e 's,includedir=${includedir},includedir=${STAGING_INCDIR},g' \
		-e 's,^INCLUDEDIR=.*,INCLUDE=${STAGING_INCDIR},g' \
		-e 's,^CONFINCLUDEDIR=.*,CONFINCLUDE=${STAGING_INCDIR},g' \
		Makefile
	# save copy of it now, because if we do it in do_install and 
	# then call do_install twice we get Makefile.orig == Makefile.sysroot
	install -m 0644 Makefile Makefile.sysroot

	export CROSS_COMPILE="${TARGET_PREFIX}"
	export PYTHONBUILDDIR="${B}"

	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python-native/python \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		OPT="${CFLAGS}"
}

do_install() {
	# make install needs the original Makefile, or otherwise the inclues would
	# go to ${D}${STAGING...}/...
	install -m 0644 Makefile.orig Makefile

	export CROSS_COMPILE="${TARGET_PREFIX}"
	export PYTHONBUILDDIR="${B}"

	# After swizzling the makefile, we need to run the build again.
	# install can race with the build so we have to run this first, then install
	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python-native/python \
		CROSSPYTHONPATH=${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/ \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		DESTDIR=${D} LIBDIR=${libdir}
	
	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python-native/python \
		CROSSPYTHONPATH=${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/ \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		DESTDIR=${D} LIBDIR=${libdir} install

	install -m 0644 Makefile.sysroot ${D}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile

	if [ -e ${WORKDIR}/sitecustomize.py ]; then
		install -m 0644 ${WORKDIR}/sitecustomize.py ${D}/${libdir}/python${PYTHON_MAJMIN}
	fi

	oe_multilib_header python${PYTHON_MAJMIN}/pyconfig.h
}

do_install_append_class-nativesdk () {
	create_wrapper ${D}${bindir}/python2.7 TERMINFO_DIRS='${sysconfdir}/terminfo:/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo'
}

SSTATE_SCAN_FILES += "Makefile"
PACKAGE_PREPROCESS_FUNCS += "py_package_preprocess"

py_package_preprocess () {
	# copy back the old Makefile to fix target package
	install -m 0644 ${B}/Makefile.orig ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile

	# Remove references to buildmachine paths in target Makefile and _sysconfigdata
	sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py
}

require python-${PYTHON_MAJMIN}-manifest.inc

# manual dependency additions
RPROVIDES_${PN}-core = "${PN}"
RRECOMMENDS_${PN}-core = "${PN}-readline"
RRECOMMENDS_${PN}-core_append_class-nativesdk = " nativesdk-python-modules"
RRECOMMENDS_${PN}-crypt = "openssl"

# package libpython2
PACKAGES =+ "lib${BPN}2"
FILES_lib${BPN}2 = "${libdir}/libpython*.so.*"

# catch debug extensions (isn't that already in python-core-dbg?)
FILES_${PN}-dbg += "${libdir}/python${PYTHON_MAJMIN}/lib-dynload/.debug"

# catch all the rest (unsorted)
PACKAGES += "${PN}-misc"
FILES_${PN}-misc = "${libdir}/python${PYTHON_MAJMIN}"
RDEPENDS_${PN}-modules += "${PN}-misc"
RDEPENDS_${PN}-ptest = "${PN}-modules"
#inherit ptest after "require python-${PYTHON_MAJMIN}-manifest.inc" so PACKAGES doesn't get overwritten
inherit ptest

# This must come after inherit ptest for the override to take effect
do_install_ptest() {
	cp ${B}/Makefile ${D}${PTEST_PATH}
	sed -e s:LIBDIR/python/ptest:${PTEST_PATH}:g \
	 -e s:LIBDIR:${libdir}:g \
	 -i ${D}${PTEST_PATH}/run-ptest
}

# catch manpage
PACKAGES += "${PN}-man"
FILES_${PN}-man = "${datadir}/man"

BBCLASSEXTEND = "nativesdk"
