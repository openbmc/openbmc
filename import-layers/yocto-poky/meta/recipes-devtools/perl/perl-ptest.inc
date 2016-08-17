inherit ptest

SRC_URI += "file://run-ptest \
           "

do_install_ptest () {
	mkdir -p ${D}${PTEST_PATH}
	sed -e "s:\/opt:\/usr:" -i Porting/add-package.pl
	sed -e "s:\/local\/gnu\/:\/:" -i hints/cxux.sh
	tar -cf - * --exclude \*.o --exclude libperl.so --exclude Makefile --exclude makefile --exclude hostperl \
		--exclude miniperl --exclude generate_uudmap --exclude patches | ( cd ${D}${PTEST_PATH} && tar -xf - )

	sed -i -e "s,${D},,g" \
	       -e "s,--sysroot=${STAGING_DIR_HOST},,g" \
	       -e "s,-isystem${STAGING_INCDIR} ,,g" \
	       -e "s,${STAGING_LIBDIR},${libdir},g" \
	       -e "s,${STAGING_BINDIR},${bindir},g" \
	       -e "s,${STAGING_INCDIR},${includedir},g" \
	       -e "s,${STAGING_BINDIR_NATIVE}/perl-native/,${bindir}/,g" \
	       -e "s,${STAGING_BINDIR_NATIVE}/,,g" \
	       -e "s,${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX},${bindir},g" \
	    ${D}${PTEST_PATH}/lib/Config.pm

	ln -sf ${bindir}/perl ${D}${PTEST_PATH}/t/perl

}

python populate_packages_prepend() {
    # Put all *.t files from the lib dir in the ptest package
    # do_split_packages requires a pair of () in the regex, but we have nothing
    # to match, so use an empty pair.
    if bb.utils.contains('DISTRO_FEATURES', 'ptest', True, False, d):
        do_split_packages(d, d.expand('${libdir}/perl/${PV}'), '.*\.t()',
            '${PN}-ptest%s', '%s', recursive=True, match_path=True)
}

RDEPENDS_${PN}-ptest += "${PN}-modules ${PN}-doc ${PN}-misc sed"
