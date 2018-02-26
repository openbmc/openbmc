require perl.inc

# We need gnugrep (for -I)
DEPENDS = "virtual/db grep-native"
DEPENDS += "gdbm zlib"

# Pick up patches from debian
# http://ftp.de.debian.org/debian/pool/main/p/perl/perl_5.22.0-1.debian.tar.xz
SRC_URI += " \
	file://debian/cpan_definstalldirs.diff \
	file://debian/db_file_ver.diff \
	file://debian/doc_info.diff \
	file://debian/enc2xs_inc.diff \
	file://debian/errno_ver.diff \
	file://debian/libperl_embed_doc.diff \
	file://debian/fixes/respect_umask.diff \
	file://debian/writable_site_dirs.diff \
	file://debian/extutils_set_libperl_path.diff \
	file://debian/no_packlist_perllocal.diff \
	file://debian/prefix_changes.diff \
	file://debian/instmodsh_doc.diff \
	file://debian/ld_run_path.diff \
	file://debian/libnet_config_path.diff \
	file://debian/mod_paths.diff \
	file://debian/prune_libs.diff \
	file://debian/fixes/net_smtp_docs.diff \
	file://debian/perlivp.diff \
	file://debian/squelch-locale-warnings.diff \
	file://debian/skip-upstream-git-tests.diff \
	file://debian/skip-kfreebsd-crash.diff \
	file://debian/fixes/document_makemaker_ccflags.diff \
	file://debian/find_html2text.diff \
	file://debian/perl5db-x-terminal-emulator.patch \
	file://debian/cpan-missing-site-dirs.diff \
	file://debian/fixes/memoize_storable_nstore.diff \
	file://debian/regen-skip.diff \
"

SRC_URI += " \
        file://Makefile.patch \
        file://Makefile.SH.patch \
        file://installperl.patch \
        file://perl-archlib-exp.patch \
        file://perl-dynloader.patch \
        file://perl-moreconfig.patch \
        file://letgcc-find-errno.patch \
        file://generate-sh.patch \
        file://native-perlinc.patch \
        file://perl-enable-gdbm.patch \
        file://cross-generate_uudmap.patch \
        file://fix_bad_rpath.patch \
        file://dynaloaderhack.patch \
        file://config.sh \
        file://config.sh-32 \
        file://config.sh-32-le \
        file://config.sh-32-be \
        file://config.sh-64 \
        file://config.sh-64-le \
        file://config.sh-64-be \
        file://make_ext.pl-fix-regenerate-makefile-failed-while-cc-.patch \
        file://t-run-switches.t-perl5-perl.patch \
        file://ext-ODBM_File-hints-linux.pl-link-libgdbm_compat.patch \
        file://ext-ODBM_File-t-odbm.t-fix-the-path-of-dbmt_common.p.patch \
        file://perl-PathTools-don-t-filter-out-blib-from-INC.patch \
        file://perl-errno-generation-gcc5.patch \
        file://perl-fix-conflict-between-skip_all-and-END.patch \
        file://perl-test-customized.patch \
"

# Fix test case issues
SRC_URI_append_class-target = " \
            file://test/dist-threads-t-join.t-adjust-ps-option.patch \
            file://test/ext-DynaLoader-t-DynaLoader.t-fix-calling-dl_findfil.patch \
           "

SRC_URI[md5sum] = "af6a84c7c3e2b8b269c105a5db2f6d53"
SRC_URI[sha256sum] = "03a77bac4505c270f1890ece75afc7d4b555090b41aa41ea478747e23b2afb3f"

inherit perlnative siteinfo

# Where to find the native perl
HOSTPERL = "${STAGING_BINDIR_NATIVE}/perl-native/perl${PV}"

# Where to find .so files - use the -native versions not those from the target build
export PERLHOSTLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PV}/"

# Where to find perl @INC/#include files
# - use the -native versions not those from the target build
export PERL_LIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PV}/"
export PERL_ARCHLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PV}/"

EXTRA_OEMAKE = "-e MAKEFLAGS="

# LDFLAGS for shared libraries
export LDDLFLAGS = "${LDFLAGS} -shared"

LDFLAGS_append = " -fstack-protector"

# We're almost Debian, aren't we?
CFLAGS += "-DDEBIAN"

do_configure() {
        # Make hostperl in build directory be the native perl
        ln -sf ${HOSTPERL} hostperl

	if [ -n "${CONFIGURESTAMPFILE}" -a -e "${CONFIGURESTAMPFILE}" ]; then
		if [ "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" -a -e Makefile ]; then
			${MAKE} clean
		fi
		find ${S} -name *.so -delete
	fi
	if [ -n "${CONFIGURESTAMPFILE}" ]; then
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi

        # Do our work in the cross subdir
        cd Cross

        # Generate configuration
        rm -f config.sh-${TARGET_ARCH}-${TARGET_OS}
        for i in ${WORKDIR}/config.sh \
                 ${WORKDIR}/config.sh-${SITEINFO_BITS} \
                 ${WORKDIR}/config.sh-${SITEINFO_BITS}-${SITEINFO_ENDIANNESS}; do
            cat $i >> config.sh-${TARGET_ARCH}-${TARGET_OS}
        done

        # Fixups for musl
        if [ "${TARGET_OS}" = "linux-musl" -o "${TARGET_OS}" = "linux-musleabi" -o "${TARGET_OS}" = "linux-muslx32" ]; then
                sed -i -e "s,\(d_libm_lib_version=\)'define',\1'undef',g" \
                       -e "s,\(d_stdio_ptr_lval=\)'define',\1'undef',g" \
                       -e "s,\(d_stdio_ptr_lval_sets_cnt=\)'define',\1'undef',g" \
                       -e "s,\(d_stdiobase=\)'define',\1'undef',g" \
                       -e "s,\(d_stdstdio=\)'define',\1'undef',g" \
                       -e "s,\(d_getnetbyname_r=\)'define',\1'undef',g" \
                       -e "s,\(d_finitel=\)'define',\1'undef',g" \
                       -e "s,\(getprotobyname_r=\)'define',\1'undef',g" \
                       -e "s,\(getpwent_r=\)'define',\1'undef',g" \
                       -e "s,\(getservent_r=\)'define',\1'undef',g" \
                       -e "s,\(gethostent_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetent_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetbyaddr_r=\)'define',\1'undef',g" \
                       -e "s,\(getprotoent_r=\)'define',\1'undef',g" \
                       -e "s,\(getprotobynumber_r=\)'define',\1'undef',g" \
                       -e "s,\(getgrent_r=\)'define',\1'undef',g" \
                       -e "s,\(i_fcntl=\)'undef',\1'define',g" \
                       -e "s,\(h_fcntl=\)'false',\1'true',g" \
                       -e "s,-fstack-protector,-fno-stack-protector,g" \
                       -e "s,-lnsl,,g" \
                    config.sh-${TARGET_ARCH}-${TARGET_OS}
        fi

        # Update some paths in the configuration
        sed -i -e 's,@ARCH@-thread-multi,,g' \
               -e 's,@ARCH@,${TARGET_ARCH}-${TARGET_OS},g' \
               -e 's,@STAGINGDIR@,${STAGING_DIR_HOST},g' \
               -e "s,@INCLUDEDIR@,${STAGING_INCDIR},g" \
               -e "s,@LIBDIR@,${libdir},g" \
               -e "s,@BASELIBDIR@,${base_libdir},g" \
               -e "s,@EXECPREFIX@,${exec_prefix},g" \
               -e 's,@USRBIN@,${bindir},g' \
            config.sh-${TARGET_ARCH}-${TARGET_OS}

	case "${TARGET_ARCH}" in
		x86_64 | powerpc | s390)
			sed -i -e "s,\(need_va_copy=\)'undef',\1'define',g" \
				config.sh-${TARGET_ARCH}-${TARGET_OS}
			;;
		arm)
			sed -i -e "s,\(d_u32align=\)'undef',\1'define',g" \
				config.sh-${TARGET_ARCH}-${TARGET_OS}
			;;
	esac
        # These are strewn all over the source tree
        for foo in `grep -I --exclude="*.patch" --exclude="*.diff" --exclude="*.pod" --exclude="README*" --exclude="Glossary" -m1 "/usr/include/.*\.h" ${S}/* -r -l` ${S}/utils/h2xs.PL ; do
            echo Fixing: $foo
            sed -e 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i $foo
        done

        rm -f config
        echo "ARCH = ${TARGET_ARCH}" > config
        echo "OS = ${TARGET_OS}" >> config
}

do_compile() {
        # Fix to avoid recursive substitution of path
        sed -i -e 's|(@libpath, ".*"|(@libpath, "${STAGING_LIBDIR}"|g' cpan/ExtUtils-MakeMaker/lib/ExtUtils/Liblist/Kid.pm

        cd Cross
        oe_runmake perl LD="${CCLD}"
}

do_install() {
	#export hostperl="${STAGING_BINDIR_NATIVE}/perl-native/perl${PV}"
	oe_runmake install DESTDIR=${D}
        # Add perl pointing at current version
        ln -sf perl${PV} ${D}${bindir}/perl

        ln -sf perl ${D}/${libdir}/perl5

        # Remove unwanted file and empty directories
        rm -f ${D}/${libdir}/perl/${PV}/.packlist
	rmdir ${D}/${libdir}/perl/site_perl/${PV}
	rmdir ${D}/${libdir}/perl/site_perl

        # Fix up shared library
        mv ${D}/${libdir}/perl/${PV}/CORE/libperl.so ${D}/${libdir}/libperl.so.${PV}
        ln -sf libperl.so.${PV} ${D}/${libdir}/libperl.so.5
        ln -sf ../../../libperl.so.${PV} ${D}/${libdir}/perl/${PV}/CORE/libperl.so

        # target config, used by cpan.bbclass to extract version information
        install config.sh ${D}${libdir}/perl

        ln -s Config_heavy.pl ${D}${libdir}/perl/${PV}/Config_heavy-target.pl
}

do_install_append_class-nativesdk () {
        create_wrapper ${D}${bindir}/perl \
            PERL5LIB='$PERL5LIB:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl/site_perl/${PV}:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl/vendor_perl/${PV}:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl/${PV}'
}

PACKAGE_PREPROCESS_FUNCS += "perl_package_preprocess"

perl_package_preprocess () {
        # Fix up installed configuration
        sed -i -e "s,${D},,g" \
               -e "s,${DEBUG_PREFIX_MAP},,g" \
               -e "s,--sysroot=${STAGING_DIR_HOST},,g" \
               -e "s,-isystem${STAGING_INCDIR} ,,g" \
               -e "s,${STAGING_LIBDIR},${libdir},g" \
               -e "s,${STAGING_BINDIR},${bindir},g" \
               -e "s,${STAGING_INCDIR},${includedir},g" \
               -e "s,${STAGING_BINDIR_NATIVE}/perl-native/,${bindir}/,g" \
               -e "s,${STAGING_BINDIR_NATIVE}/,,g" \
               -e "s,${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX},${bindir},g" \
            ${PKGD}${bindir}/h2xs \
            ${PKGD}${bindir}/h2ph \
            ${PKGD}${bindir}/pod2man \
            ${PKGD}${bindir}/pod2text \
            ${PKGD}${bindir}/pod2usage \
            ${PKGD}${bindir}/podchecker \
            ${PKGD}${bindir}/podselect \
            ${PKGD}${libdir}/perl/${PV}/CORE/config.h \
            ${PKGD}${libdir}/perl/${PV}/CORE/perl.h \
            ${PKGD}${libdir}/perl/${PV}/CORE/pp.h \
            ${PKGD}${libdir}/perl/${PV}/Config.pm \
            ${PKGD}${libdir}/perl/${PV}/Config.pod \
            ${PKGD}${libdir}/perl/${PV}/Config_heavy.pl \
            ${PKGD}${libdir}/perl/${PV}/ExtUtils/Liblist/Kid.pm \
            ${PKGD}${libdir}/perl/${PV}/FileCache.pm \
            ${PKGD}${libdir}/perl/${PV}/pod/*.pod \
            ${PKGD}${libdir}/perl/config.sh
}

PACKAGES = "perl-dbg perl perl-misc perl-dev perl-pod perl-doc perl-lib \
            perl-module-cpan perl-module-cpanplus perl-module-unicore"
FILES_${PN} = "${bindir}/perl ${bindir}/perl${PV} \
               ${libdir}/perl/${PV}/Config.pm \
               ${libdir}/perl/${PV}/strict.pm \
               ${libdir}/perl/${PV}/warnings.pm \
               ${libdir}/perl/${PV}/warnings \
               ${libdir}/perl/${PV}/vars.pm \
              "
FILES_${PN}_append_class-nativesdk = " ${bindir}/perl.real"
RPROVIDES_${PN} += "perl-module-strict perl-module-vars perl-module-config perl-module-warnings \
                    perl-module-warnings-register"
FILES_${PN}-dev = "${libdir}/perl/${PV}/CORE"
FILES_${PN}-lib = "${libdir}/libperl.so* \
                   ${libdir}/perl5 \
                   ${libdir}/perl/config.sh \
                   ${libdir}/perl/${PV}/Config_heavy.pl \
                   ${libdir}/perl/${PV}/Config_heavy-target.pl"
FILES_${PN}-pod = "${libdir}/perl/${PV}/pod \
		   ${libdir}/perl/${PV}/*.pod \
                   ${libdir}/perl/${PV}/*/*.pod \
                   ${libdir}/perl/${PV}/*/*/*.pod "
FILES_perl-misc = "${bindir}/*"
FILES_${PN}-doc = "${libdir}/perl/${PV}/*/*.txt \
                   ${libdir}/perl/${PV}/*/*/*.txt \
                   ${libdir}/perl/${PV}/auto/XS/Typemap \
                   ${libdir}/perl/${PV}/B/assemble \
                   ${libdir}/perl/${PV}/B/cc_harness \
                   ${libdir}/perl/${PV}/B/disassemble \
                   ${libdir}/perl/${PV}/B/makeliblinks \
                   ${libdir}/perl/${PV}/CGI/eg \
                   ${libdir}/perl/${PV}/CPAN/PAUSE2003.pub \
                   ${libdir}/perl/${PV}/CPAN/SIGNATURE \
		   ${libdir}/perl/${PV}/CPANPLUS/Shell/Default/Plugins/HOWTO.pod \
                   ${libdir}/perl/${PV}/Encode/encode.h \
                   ${libdir}/perl/${PV}/ExtUtils/MANIFEST.SKIP \
                   ${libdir}/perl/${PV}/ExtUtils/NOTES \
                   ${libdir}/perl/${PV}/ExtUtils/PATCHING \
                   ${libdir}/perl/${PV}/ExtUtils/typemap \
                   ${libdir}/perl/${PV}/ExtUtils/xsubpp \
		   ${libdir}/perl/${PV}/ExtUtils/Changes_EU-Install \
                   ${libdir}/perl/${PV}/Net/*.eg \
                   ${libdir}/perl/${PV}/unicore/mktables \
                   ${libdir}/perl/${PV}/unicore/mktables.lst \
                   ${libdir}/perl/${PV}/unicore/version "

FILES_perl-module-cpan += "${libdir}/perl/${PV}/CPAN \
                           ${libdir}/perl/${PV}/CPAN.pm"
FILES_perl-module-cpanplus += "${libdir}/perl/${PV}/CPANPLUS \
                               ${libdir}/perl/${PV}/CPANPLUS.pm"
FILES_perl-module-unicore += "${libdir}/perl/${PV}/unicore"

# Create a perl-modules package recommending all the other perl
# packages (actually the non modules packages and not created too)
ALLOW_EMPTY_perl-modules = "1"
PACKAGES_append = " perl-modules "

PACKAGESPLITFUNCS_prepend = "split_perl_packages "

python split_perl_packages () {
    libdir = d.expand('${libdir}/perl/${PV}')
    do_split_packages(d, libdir, 'auto/([^.]*)/[^/]*\.(so|ld|ix|al)', 'perl-module-%s', 'perl module %s', recursive=True, match_path=True, prepend=False)
    do_split_packages(d, libdir, 'Module/([^\/]*)\.pm', 'perl-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, 'Module/([^\/]*)/.*', 'perl-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, '(^(?!(CPAN\/|CPANPLUS\/|Module\/|unicore\/|auto\/)[^\/]).*)\.(pm|pl|e2x)', 'perl-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)

    # perl-modules should recommend every perl module, and only the
    # modules. Don't attempt to use the result of do_split_packages() as some
    # modules are manually split (eg. perl-module-unicore).
    packages = filter(lambda p: 'perl-module-' in p, d.getVar('PACKAGES').split())
    d.setVar(d.expand("RRECOMMENDS_${PN}-modules"), ' '.join(packages))
}

PACKAGES_DYNAMIC += "^perl-module-.*"
PACKAGES_DYNAMIC_class-nativesdk += "^nativesdk-perl-module-.*"

RPROVIDES_perl-lib = "perl-lib"

require perl-rdepends_${PV}.inc
require perl-ptest.inc

SSTATE_SCAN_FILES += "*.pm *.pod *.h *.pl *.sh"

BBCLASSEXTEND = "nativesdk"
