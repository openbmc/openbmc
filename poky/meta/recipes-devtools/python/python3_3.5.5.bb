require recipes-devtools/python/python.inc

DEPENDS = "python3-native libffi bzip2 gdbm openssl sqlite3 zlib virtual/libintl xz"

PR = "${INC_PR}.0"
PYTHON_MAJMIN = "3.5"
PYTHON_BINABI= "${PYTHON_MAJMIN}m"
DISTRO_SRC_URI ?= "file://sitecustomize.py"
DISTRO_SRC_URI_linuxstdbase = ""
SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz \
file://python-config.patch \
file://0001-cross-compile-support.patch \
file://030-fixup-include-dirs.patch \
file://070-dont-clean-ipkg-install.patch \
file://080-distutils-dont_adjust_files.patch \
file://130-readline-setup.patch \
file://150-fix-setupterm.patch \
file://0001-h2py-Fix-issue-13032-where-it-fails-with-UnicodeDeco.patch \
file://tweak-MULTIARCH-for-powerpc-linux-gnuspe.patch \
file://support_SOURCE_DATE_EPOCH_in_py_compile.patch \
${DISTRO_SRC_URI} \
"

SRC_URI += "\
            file://03-fix-tkinter-detection.patch \
            file://avoid_warning_about_tkinter.patch \
            file://cgi_py.patch \
            file://host_include_contamination.patch \
            file://python-3.3-multilib.patch \
            file://shutil-follow-symlink-fix.patch \
            file://sysroot-include-headers.patch \
            file://unixccompiler.patch \
            file://avoid-ncursesw-include-path.patch \
            file://python3-use-CROSSPYTHONPATH-for-PYTHON_FOR_BUILD.patch \
            file://sysconfig.py-add-_PYTHON_PROJECT_SRC.patch \
            file://setup.py-check-cross_compiling-when-get-FLAGS.patch \
            file://configure.ac-fix-LIBPL.patch \
            file://0001-Issue-21272-Use-_sysconfigdata.py-to-initialize-dist.patch \
            file://pass-missing-libraries-to-Extension-for-mul.patch \
            file://CVE-2018-1061.patch \
           "
SRC_URI[md5sum] = "f3763edf9824d5d3a15f5f646083b6e0"
SRC_URI[sha256sum] = "063d2c3b0402d6191b90731e0f735c64830e7522348aeb7ed382a83165d45009"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b6ec515b22618f55fa07276b897bacea"

# exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"

S = "${WORKDIR}/Python-${PV}"

inherit autotools multilib_header python3native pkgconfig

CONFIGUREOPTS += " --with-system-ffi "

CACHED_CONFIGUREVARS = "ac_cv_have_chflags=no \
                ac_cv_have_lchflags=no \
                ac_cv_have_long_long_format=yes \
                ac_cv_buggy_getaddrinfo=no \
                ac_cv_file__dev_ptmx=yes \
                ac_cv_file__dev_ptc=no \
"

TARGET_CC_ARCH += "-DNDEBUG -fno-inline"
SDK_CC_ARCH += "-DNDEBUG -fno-inline"
EXTRA_OEMAKE += "CROSS_COMPILE=yes"
EXTRA_OECONF += "CROSSPYTHONPATH=${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/ --without-ensurepip"

export CROSS_COMPILE = "${TARGET_PREFIX}"
export _PYTHON_PROJECT_BASE = "${B}"
export _PYTHON_PROJECT_SRC = "${S}"
export CCSHARED = "-fPIC"

# Fix cross compilation of different modules
export CROSSPYTHONPATH = "${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/:${B}/build/lib.linux-${TARGET_ARCH}-${PYTHON_MAJMIN}:${S}/Lib:${S}/Lib/plat-linux"

# No ctypes option for python 3
PYTHONLSBOPTS = ""

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = ",,readline"

do_configure_append() {
	rm -f ${S}/Makefile.orig
	autoreconf -Wcross --verbose --install --force --exclude=autopoint ../Python-${PV}/Modules/_ctypes/libffi
}

do_compile() {
        # regenerate platform specific files, because they depend on system headers
        cd ${S}/Lib/plat-linux*
        include=${STAGING_INCDIR} ${STAGING_BINDIR_NATIVE}/python3-native/python3 \
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
	sed -i -e 's,^CONFIGURE_LDFLAGS=.*,CONFIGURE_LDFLAGS=-L. -L${STAGING_LIBDIR},g' \
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

	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		OPT="${CFLAGS}" libpython3.so

	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		OPT="${CFLAGS}"
}

do_install() {
	# make install needs the original Makefile, or otherwise the inclues would
	# go to ${D}${STAGING...}/...
	install -m 0644 Makefile.orig Makefile

	install -d ${D}${libdir}/pkgconfig
	install -d ${D}${libdir}/python${PYTHON_MAJMIN}/config

	# rerun the build once again with original makefile this time
	# run install in a separate step to avoid compile/install race
	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		DESTDIR=${D} LIBDIR=${libdir}
	
	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		DESTDIR=${D} LIBDIR=${libdir} install

	# avoid conflict with 2to3 from Python 2
	rm -f ${D}/${bindir}/2to3

	install -m 0644 Makefile.sysroot ${D}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile
	install -m 0644 Makefile.sysroot ${D}/${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_MAJMIN}${PYTHON_ABI}/Makefile

	if [ -e ${WORKDIR}/sitecustomize.py ]; then
		install -m 0644 ${WORKDIR}/sitecustomize.py ${D}/${libdir}/python${PYTHON_MAJMIN}
	fi

	oe_multilib_header python${PYTHON_BINABI}/pyconfig.h
}

do_install_append_class-nativesdk () {
	create_wrapper ${D}${bindir}/python${PYTHON_MAJMIN} TERMINFO_DIRS='${sysconfdir}/terminfo:/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo' PYTHONNOUSERSITE='1'
}

SSTATE_SCAN_FILES += "Makefile"
PACKAGE_PREPROCESS_FUNCS += "py_package_preprocess"

py_package_preprocess () {
	# copy back the old Makefile to fix target package
	install -m 0644 ${B}/Makefile.orig ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile
	install -m 0644 ${B}/Makefile.orig ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_MAJMIN}${PYTHON_ABI}/Makefile
	# Remove references to buildmachine paths in target Makefile and _sysconfigdata
	sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
		-e 's|${DEBUG_PREFIX_MAP}||g' \
		-e 's:${HOSTTOOLS_DIR}/::g' \
		-e 's:${RECIPE_SYSROOT_NATIVE}::g' \
		-e 's:${RECIPE_SYSROOT}::g' \
		-e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_MAJMIN}${PYTHON_ABI}/Makefile \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py \
		${PKGD}/${bindir}/python${PYTHON_BINABI}-config

	# Recompile _sysconfigdata after modifying it
	cd ${PKGD}
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
	     -c "from py_compile import compile; compile('./${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py')"
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
	     -c "from py_compile import compile; compile('./${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py', optimize=1)"
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
	     -c "from py_compile import compile; compile('./${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py', optimize=2)"
	cd -
}

# manual dependency additions
RPROVIDES_${PN}-modules = "${PN}"
RRECOMMENDS_${PN}-core_append_class-nativesdk = " nativesdk-python3-modules"
RRECOMMENDS_${PN}-crypt = "openssl"
RRECOMMENDS_${PN}-crypt_class-nativesdk = "nativesdk-openssl"

FILES_${PN}-2to3 += "${bindir}/2to3-${PYTHON_MAJMIN}"
FILES_${PN}-pydoc += "${bindir}/pydoc${PYTHON_MAJMIN} ${bindir}/pydoc3"
FILES_${PN}-idle += "${bindir}/idle3 ${bindir}/idle${PYTHON_MAJMIN}"

PACKAGES =+ "${PN}-pyvenv"
FILES_${PN}-pyvenv += "${bindir}/pyvenv-${PYTHON_MAJMIN} ${bindir}/pyvenv"

# package libpython3
PACKAGES =+ "libpython3 libpython3-staticdev"
FILES_libpython3 = "${libdir}/libpython*.so.*"
FILES_libpython3-staticdev += "${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_BINABI}/libpython${PYTHON_BINABI}.a"
INSANE_SKIP_${PN}-dev += "dev-elf"

# catch all the rest (unsorted)
PACKAGES += "${PN}-misc"
RDEPENDS_${PN}-misc += "${PN}-core ${PN}-email ${PN}-codecs"
RDEPENDS_${PN}-modules += "${PN}-misc"
FILES_${PN}-misc = "${libdir}/python${PYTHON_MAJMIN}"

# catch manpage
PACKAGES += "${PN}-man"
FILES_${PN}-man = "${datadir}/man"

BBCLASSEXTEND = "nativesdk"

RPROVIDES_${PN} += "${PN}-modules"

# We want bytecode precompiled .py files (.pyc's) by default
# but the user may set it on their own conf
INCLUDE_PYCS ?= "1"

python(){
    import json

    filename = os.path.join(d.getVar('THISDIR'), 'python3', 'python3-manifest.json')
    # This python changes the datastore based on the contents of a file, so mark
    # that dependency.
    bb.parse.mark_dependency(d, filename)

    with open(filename) as manifest_file:
        python_manifest=json.load(manifest_file)

    include_pycs = d.getVar('INCLUDE_PYCS')

    packages = d.getVar('PACKAGES').split()
    pn = d.getVar('PN')

    newpackages=[]
    for key in python_manifest:
        pypackage= pn + '-' + key

        if pypackage not in packages:
            # We need to prepend, otherwise python-misc gets everything
            # so we use a new variable
            newpackages.append(pypackage)

        # "Build" python's manifest FILES, RDEPENDS and SUMMARY
        d.setVar('FILES_' + pypackage, '')
        for value in python_manifest[key]['files']:
            d.appendVar('FILES_' + pypackage, ' ' + value)

    	# Add cached files
        if include_pycs == '1':
            for value in python_manifest[key]['cached']:
                    d.appendVar('FILES_' + pypackage, ' ' + value)

        d.setVar('RDEPENDS_' + pypackage, '')
        for value in python_manifest[key]['rdepends']:
            # Make it work with or without $PN
            if '${PN}' in value:
                value=value.split('-')[1]
            d.appendVar('RDEPENDS_' + pypackage, ' ' + pn + '-' + value)
        d.setVar('SUMMARY_' + pypackage, python_manifest[key]['summary'])

    # We need to ensure staticdev packages match for files first so we sort in reverse
    newpackages.sort(reverse=True)
    # Prepending so to avoid python-misc getting everything
    packages = newpackages + packages
    d.setVar('PACKAGES', ' '.join(packages))
    d.setVar('ALLOW_EMPTY_${PN}-modules', '1')
}

# Files needed to create a new manifest
SRC_URI += "file://create_manifest3.py file://get_module_deps3.py file://python3-manifest.json"

do_create_manifest() {
    # This task should be run with every new release of Python.
    # We must ensure that PACKAGECONFIG enables everything when creating
    # a new manifest, this is to base our new manifest on a complete
    # native python build, containing all dependencies, otherwise the task
    # wont be able to find the required files.
    # e.g. BerkeleyDB is an optional build dependency so it may or may not
    # be present, we must ensure it is.

    cd ${WORKDIR}
    # This needs to be executed by python-native and NOT by HOST's python
    nativepython3 create_manifest3.py
    cp python3-manifest.json.new ${THISDIR}/python3/python3-manifest.json
}

# bitbake python -c create_manifest
addtask do_create_manifest

# Make sure we have native python ready when we create a new manifest
do_create_manifest[depends] += "python3:do_prepare_recipe_sysroot"
do_create_manifest[depends] += "python3:do_patch"
