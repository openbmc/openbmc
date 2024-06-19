# Note: Due to the bitbake wrapper script, making changes to pseudo can be
# difficult.  To work around the current version of the wrapper use:
# BBFETCH2=True PSEUDO_BUILD=1 ../bitbake/bin/bitbake pseudo-native [-c CMD]

SUMMARY = "Pseudo gives fake root capabilities to a normal user"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/pseudo"
DESCRIPTION = "The pseudo utility offers a way to run commands in a virtualized root environment."
LIC_FILES_CHKSUM = "file://COPYING;md5=a1d8023a6f953ac6ea4af765ff62d574"
SECTION = "base"
LICENSE = "LGPL-2.1-only"
DEPENDS = "sqlite3 attr"

FILES:${PN} = "${prefix}/lib/pseudo/lib*/libpseudo.so ${bindir}/* ${localstatedir}/pseudo ${prefix}/var/pseudo"
INSANE_SKIP:${PN} += "libdir"
INSANE_SKIP:${PN}-dbg += "libdir"

PROVIDES += "virtual/fakeroot"

MAKEOPTS = ""
MAKEOPTS:class-native = "'RPATH=-Wl,--rpath=XORIGIN/../../../sqlite3-native/usr/lib/'"

inherit siteinfo pkgconfig

do_configure () {
	:
}

NO32LIBS ??= "1"
NO32LIBS:class-nativesdk = "1"

PSEUDO_EXTRA_OPTS ?= "--enable-force-async --without-passwd-fallback --enable-epoll --enable-xattr"

# Compile for the local machine arch...
do_compile () {
	if [ "${SITEINFO_BITS}" = "64" ]; then
	  ${S}/configure ${PSEUDO_EXTRA_OPTS} --prefix=${prefix} --libdir=${prefix}/lib/pseudo/lib${SITEINFO_BITS} --with-sqlite-lib=${baselib} --with-sqlite=${STAGING_DIR_TARGET}${exec_prefix} --cflags="${CFLAGS}" --bits=${SITEINFO_BITS} --without-rpath
	else
	  ${S}/configure ${PSEUDO_EXTRA_OPTS} --prefix=${prefix} --libdir=${prefix}/lib/pseudo/lib --with-sqlite-lib=${baselib} --with-sqlite=${STAGING_DIR_TARGET}${exec_prefix} --cflags="${CFLAGS}" --bits=${SITEINFO_BITS} --without-rpath
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
do_compile:prepend:class-native () {
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

do_compile:prepend:class-nativesdk () {
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

do_compile:append:class-native () {
	if [ '${@bb.data.inherits_class('uninative', d)}' = 'True' ]; then
		for i in PSEUDO_PORT_UNIX_SYNCFS PSEUDO_PORT_UIDS_GENERIC PSEUDO_PORT_LINUX_NEWCLONE PSEUDO_PORT_LINUX_XATTR PSEUDO_PORT_LINUX_STATVFS; do
			grep $i.1 ${S}/pseudo_ports.h
			if [ $? != 0 ]; then
				echo "$i not enabled in pseudo which is incompatible with uninative"
				exit 1
			fi
		done
	fi
}


do_install () {
	oe_runmake 'DESTDIR=${D}' ${MAKEOPTS} 'LIB=lib/pseudo/lib$(MARK64)' install
}

do_install:append:class-native () {
	chrpath ${D}${bindir}/pseudo -r `chrpath ${D}${bindir}/pseudo | cut -d = -f 2 | sed s/XORIGIN/\\$ORIGIN/`
	install -d ${D}${sysconfdir}
	# The fallback files should never be modified
	install -m 444 ${UNPACKDIR}/fallback-passwd ${D}${sysconfdir}/passwd
	install -m 444 ${UNPACKDIR}/fallback-group ${D}${sysconfdir}/group

	# Two native/nativesdk entries below are the same
	# If necessary install for the alternative machine arch.  This is only
	# necessary in a native build.
	maybe_make32
	if $make32; then
		mkdir -p ${D}${prefix}/lib/pseudo/lib
		cp lib/pseudo/lib/libpseudo.so ${D}${prefix}/lib/pseudo/lib/.
	fi
}

do_install:append:class-nativesdk () {
	maybe_make32
	if $make32; then
		mkdir -p ${D}${prefix}/lib/pseudo/lib
		cp lib/pseudo/lib/libpseudo.so ${D}${prefix}/lib/pseudo/lib/.
	fi
	chrpath -d ${D}${prefix}/lib/pseudo/lib*/libpseudo.so
}

BBCLASSEXTEND = "native nativesdk"
