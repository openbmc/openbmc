# Note: Due to the bitbake wrapper script, making changes to pseudo can be
# difficult.  To work around the current version of the wrapper use:
# BBFETCH2=True PSEUDO_BUILD=1 ../bitbake/bin/bitbake pseudo-native [-c CMD]

SUMMARY = "Pseudo gives fake root capabilities to a normal user"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/pseudo"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"
SECTION = "base"
LICENSE = "LGPL2.1"
DEPENDS = "sqlite3 attr"

FILES_${PN} = "${prefix}/lib/pseudo/lib*/libpseudo.so ${bindir}/* ${localstatedir}/pseudo ${prefix}/var/pseudo"
INSANE_SKIP_${PN} += "libdir"
INSANE_SKIP_${PN}-dbg += "libdir"

PROVIDES += "virtual/fakeroot"

MAKEOPTS = ""

inherit siteinfo

do_configure () {
	:
}

NO32LIBS ??= "1"
NO32LIBS_class-nativesdk = "1"

PSEUDO_EXTRA_OPTS ?= "--enable-force-async --without-passwd-fallback"

# Compile for the local machine arch...
do_compile () {
	if [ "${SITEINFO_BITS}" = "64" ]; then
	  ${S}/configure ${PSEUDO_EXTRA_OPTS} --prefix=${prefix} --libdir=${prefix}/lib/pseudo/lib${SITEINFO_BITS} --with-sqlite-lib=${baselib} --with-sqlite=${STAGING_DIR_TARGET}${exec_prefix} --cflags="${CFLAGS}" --bits=${SITEINFO_BITS} --enable-static-sqlite --without-rpath
	else
	  ${S}/configure ${PSEUDO_EXTRA_OPTS} --prefix=${prefix} --libdir=${prefix}/lib/pseudo/lib --with-sqlite-lib=${baselib} --with-sqlite=${STAGING_DIR_TARGET}${exec_prefix} --cflags="${CFLAGS}" --bits=${SITEINFO_BITS} --enable-static-sqlite --without-rpath
	fi
	oe_runmake ${MAKEOPTS}
}
do_compile[vardepsexclude] = "SITEINFO_BITS"

maybe_make32() {
	# We probably don't need to build 32-bit binaries.
	make32=false
	if [ "${SITEINFO_BITS}" = "64" ]; then
		case "${NO32LIBS}" in
		0)	make32=true
			;;
		1)	make32=false
			;;
		*)	# If unset, build 32-bit if we think we can.
			if [ -e "/usr/include/gnu/stubs-32.h" ]; then
				make32=true
			fi
			;;
		esac
	fi
	if $make32; then
		if ! [ -e "/usr/include/gnu/stubs-32.h" ]; then
			warn_32bit_missing
		else
			bbnote "Attempting to build 32-bit libpseudo.so for ${PN}."
		fi
	else
		bbnote "Building/installing only 64-bit libpseudo.so for ${PN}."
		bbnote "If you need to run 32-bit executables, ensure that NO32LIBS is set to 0."
	fi
}
maybe_make32[vardepsexclude] = "SITEINFO_BITS"

warn_32bit_missing() {
	bbwarn "Can't find stubs-32.h, but usually need it to build 32-bit libpseudo."
	bbwarn "If the build fails, install 32-bit developer packages."
	bbwarn "If you are using 32-bit binaries, the 32-bit libpseudo is NOT optional."
}

# Two below are the same
# If necessary compile for the alternative machine arch.  This is only 
# necessary in a native build.
do_compile_prepend_class-native () {
	maybe_make32
	if $make32; then
		# We need the 32-bit libpseudo on a 64-bit machine...
		# Note that this is not well-tested outside of x86/x86_64.

		# if we're being rebuilt due to a dependency change, we need to make sure
		# everything is clean before we configure and build -- if we haven't previously
		# built this will fail and be ignored.
		make ${MAKEOPTS} distclean || :

		./configure ${PSEUDO_EXTRA_OPTS} --prefix=${prefix} --libdir=${prefix}/lib/pseudo/lib --with-sqlite-lib=${baselib} --with-sqlite=${STAGING_DIR_TARGET}${exec_prefix} --bits=32 --without-rpath
		save_traps=$(trap)
		trap 'warn_32bit_missing' 0
		oe_runmake ${MAKEOPTS} libpseudo
		eval "$save_traps"
		# prevent it from removing the lib, but remove everything else
		make 'LIB=foo' ${MAKEOPTS} distclean 
	fi
}

do_compile_prepend_class-nativesdk () {
	maybe_make32
	if $make32; then
		# We need the 32-bit libpseudo on a 64-bit machine.
		# Note that this is not well-tested outside of x86/x86_64.
		./configure ${PSEUDO_EXTRA_OPTS} --prefix=${prefix} --libdir=${prefix}/lib/pseudo/lib --with-sqlite-lib=${baselib} --with-sqlite=${STAGING_DIR_TARGET}${exec_prefix} --bits=32 --without-rpath
		oe_runmake ${MAKEOPTS} libpseudo
		# prevent it from removing the lib, but remove everything else
		make 'LIB=foo' ${MAKEOPTS} distclean 
	fi
}

do_install () {
	oe_runmake 'DESTDIR=${D}' ${MAKEOPTS} 'LIB=lib/pseudo/lib$(MARK64)' install
}

do_install_append_class-native () {
	install -d ${D}${sysconfdir}
	# The fallback files should never be modified
	install -m 444 ${WORKDIR}/fallback-passwd ${D}${sysconfdir}/passwd
	install -m 444 ${WORKDIR}/fallback-group ${D}${sysconfdir}/group

	# Two native/nativesdk entries below are the same
	# If necessary install for the alternative machine arch.  This is only
	# necessary in a native build.
	maybe_make32
	if $make32; then
		mkdir -p ${D}${prefix}/lib/pseudo/lib
		cp lib/pseudo/lib/libpseudo.so ${D}${prefix}/lib/pseudo/lib/.
	fi
}

do_install_append_class-nativesdk () {
	maybe_make32
	if $make32; then
		mkdir -p ${D}${prefix}/lib/pseudo/lib
		cp lib/pseudo/lib/libpseudo.so ${D}${prefix}/lib/pseudo/lib/.
	fi
	chrpath -d ${D}${prefix}/lib/pseudo/lib*/libpseudo.so
}

BBCLASSEXTEND = "native nativesdk"
