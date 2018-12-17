require perl.inc

# We need gnugrep (for -I)
DEPENDS = "db-native grep-native gdbm-native zlib-native"

EXTRA_OEMAKE = "-e MAKEFLAGS="

SRC_URI += "\
           file://Configure-multilib.patch \
           file://perl-configpm-switch.patch \
           file://native-nopacklist.patch \
           file://native-perlinc.patch \
           file://MM_Unix.pm.patch \
           file://debian/errno_ver.diff \
           file://dynaloaderhack.patch \
           file://perl-PathTools-don-t-filter-out-blib-from-INC.patch \
           file://0001-Configure-Remove-fstack-protector-strong-for-native-.patch \
           file://perl-5.26.1-guard_old_libcrypt_fix.patch \
           file://0001-ExtUtils-MM_Unix.pm-fix-race-issues.patch \
          "

SRC_URI[md5sum] = "04622bc4d3941dc7eb571c52b7c02993"
SRC_URI[sha256sum] = "7f080287ff64750270689843ae945f02159a33cb8f2fc910248c15befba5db84"

inherit native

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

export LD="${CCLD}"

do_configure () {
	./Configure \
		-Dcc="${CC}" \
		-Dcflags="${CFLAGS}" \
		-Dldflags="${LDFLAGS}" \
		-Dlddlflags="${LDFLAGS} -shared" \
		-Dcf_by="Open Embedded" \
		-Dprefix=${prefix} \
		-Dvendorprefix=${prefix} \
		-Dsiteprefix=${prefix} \
		\
		-Dbin=${STAGING_BINDIR}/${PN} \
		-Dprivlib=${STAGING_LIBDIR}/perl/${PV} \
		-Darchlib=${STAGING_LIBDIR}/perl/${PV} \
		-Dvendorlib=${STAGING_LIBDIR}/perl/vendor_perl/${PV} \
		-Dvendorarch=${STAGING_LIBDIR}/perl/vendor_perl/${PV} \
		-Dsitelib=${STAGING_LIBDIR}/perl/site_perl/${PV} \
		-Dsitearch=${STAGING_LIBDIR}/perl/site_perl/${PV} \
		\
		-Duseshrplib \
		-Dusethreads \
		-Duseithreads \
		-Duselargefiles \
		-Dnoextensions=ODBM_File \
		-Ud_dosuid \
		-Ui_db \
		-Ui_ndbm \
		-Ui_gdbm \
		-Ui_gdbm_ndbm \
		-Ui_gdbmndbm \
		-Di_shadow \
		-Di_syslog \
		-Duseperlio \
		-Dman3ext=3pm \
		-Dsed=/bin/sed \
		-Uafs \
		-Ud_csh \
		-Uusesfio \
		-Uusenm -des
}

do_install () {
	oe_runmake 'DESTDIR=${D}' install

	# We need a hostperl link for building perl
	ln -sf perl${PV} ${D}${bindir}/hostperl

        ln -sf perl ${D}${libdir}/perl5

	install -d ${D}${libdir}/perl/${PV}/CORE \
	           ${D}${datadir}/perl/${PV}/ExtUtils

	# Save native config 
	install config.sh ${D}${libdir}/perl
	install lib/Config.pm ${D}${libdir}/perl/${PV}/
	install lib/ExtUtils/typemap ${D}${libdir}/perl/${PV}/ExtUtils/

	# perl shared library headers
	# reference perl 5.20.0-1 in debian:
	# https://packages.debian.org/experimental/i386/perl/filelist
	for i in av.h bitcount.h charclass_invlists.h config.h cop.h cv.h dosish.h \
		embed.h embedvar.h EXTERN.h fakesdio.h feature.h form.h git_version.h \
		gv.h handy.h hv_func.h hv.h inline.h INTERN.h intrpvar.h iperlsys.h \
		keywords.h l1_char_class_tab.h malloc_ctl.h metaconfig.h mg_data.h \
		mg.h mg_raw.h mg_vtable.h mydtrace.h nostdio.h opcode.h op.h \
		opnames.h op_reg_common.h overload.h pad.h parser.h patchlevel.h \
		perlapi.h perl.h perlio.h perliol.h perlsdio.h perlvars.h perly.h \
		pp.h pp_proto.h proto.h reentr.h regcharclass.h regcomp.h regexp.h \
		regnodes.h scope.h sv.h thread.h time64_config.h time64.h uconfig.h \
		unicode_constants.h unixish.h utf8.h utfebcdic.h util.h uudmap.h \
		vutil.h warnings.h XSUB.h
	do
		install $i ${D}${libdir}/perl/${PV}/CORE
	done

	# Those wrappers mean that perl installed from sstate (which may change
	# path location) works and that in the nativesdk case, the SDK can be
	# installed to a different location from the one it was built for.
	create_wrapper ${D}${bindir}/perl PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl/site_perl/${PV}:${STAGING_LIBDIR}/perl/vendor_perl/${PV}:${STAGING_LIBDIR}/perl/${PV}'
	create_wrapper ${D}${bindir}/perl${PV} PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl/site_perl/${PV}:${STAGING_LIBDIR}/perl/vendor_perl/${PV}:${STAGING_LIBDIR}/perl/${PV}'

	# Use /usr/bin/env nativeperl for the perl script.
	for f in `grep -Il '#! *${bindir}/perl' ${D}/${bindir}/*`; do
		sed -i -e 's|${bindir}/perl|/usr/bin/env nativeperl|' $f
	done

	# The packlist is large with hardcoded paths meaning it needs relocating
	# so just remove it.
	rm ${D}${libdir}/perl/${PV}/.packlist
}

SYSROOT_PREPROCESS_FUNCS += "perl_sysroot_create_wrapper"

perl_sysroot_create_wrapper () {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	# Create a wrapper that /usr/bin/env perl will use to get perl-native.
	# This MUST live in the normal bindir.
	cat > ${SYSROOT_DESTDIR}${bindir}/../nativeperl << EOF
#!/bin/sh
realpath=\`readlink -fn \$0\`
exec \`dirname \$realpath\`/perl-native/perl "\$@"
EOF
	chmod 0755 ${SYSROOT_DESTDIR}${bindir}/../nativeperl
	cat ${SYSROOT_DESTDIR}${bindir}/../nativeperl
}

# Fix the path in sstate
SSTATE_SCAN_FILES += "*.pm *.pod *.h *.pl *.sh"

PACKAGES_DYNAMIC_class-native = "^perl-module-.*native$"
