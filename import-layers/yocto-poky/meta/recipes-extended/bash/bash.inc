SUMMARY = "An sh-compatible command language interpreter"
HOMEPAGE = "http://tiswww.case.edu/php/chet/bash/bashtop.html"
SECTION = "base/shell"

DEPENDS = "ncurses bison-native virtual/libiconv"

inherit autotools gettext texinfo update-alternatives ptest

EXTRA_AUTORECONF += "--exclude=autoheader"
EXTRA_OECONF = "--enable-job-control --without-bash-malloc"

# If NON_INTERACTIVE_LOGIN_SHELLS is defined, all login shells read the
# startup files, even if they are not interactive.
# This is what other major distros do. And this is what we wanted. See bug#5359 and bug#7137.
CFLAGS += "-DNON_INTERACTIVE_LOGIN_SHELLS"

ALTERNATIVE_${PN} = "bash sh"
ALTERNATIVE_LINK_NAME[bash] = "${base_bindir}/bash"
ALTERNATIVE_TARGET[bash] = "${base_bindir}/bash"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/bash.${BPN}"
ALTERNATIVE_PRIORITY = "100"

RDEPENDS_${PN} += "base-files"
RDEPENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}-ptest += "make"

DEPENDS_append_libc-glibc = " glibc-locale"
RDEPENDS_${PN}-ptest_append_libc-glibc = " locale-base-fr-fr locale-base-de-de"

USERADD_PACKAGES = "${PN}-ptest"
USERADD_PARAM_${PN}-ptest = "--create-home --user-group test"

CACHED_CONFIGUREVARS += "headersdir=${includedir}/${PN}"

do_configure_prepend () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		cat ${S}/aclocal.m4 > ${S}/acinclude.m4
	fi
}

do_compile_ptest () {
	oe_runmake buildtest
}

do_install_append () {
	# Move /usr/bin/bash to /bin/bash, if need
	if [ "${base_bindir}" != "${bindir}" ]; then
		mkdir -p ${D}${base_bindir}
		mv ${D}${bindir}/bash ${D}${base_bindir}
	fi
}
do_install_append_class-target () {
	# Clean buildhost references in bashbug
	sed -i -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
		-e "s,-I${WORKDIR}/\S* ,,g" \
		-e 's|${DEBUG_PREFIX_MAP}||g' \
		${D}${bindir}/bashbug

	# Clean buildhost references in bash.pc
	sed -i -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
	     ${D}${libdir}/pkgconfig/bash.pc

	# Clean buildhost references in Makefile.inc
	sed -i -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
		-e 's|${DEBUG_PREFIX_MAP}||g' \
		-e 's:${HOSTTOOLS_DIR}/::g' \
		-e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
		${D}${libdir}/bash/Makefile.inc
}

do_install_ptest () {
	make INSTALL_TEST_DIR=${D}${PTEST_PATH}/tests install-test
	cp ${B}/Makefile ${D}${PTEST_PATH}
        sed -i -e 's/^Makefile/_Makefile/' -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e "s,${S},,g" -e "s,${B},,g" -e "s,${STAGING_DIR_NATIVE},,g" \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	     ${D}${PTEST_PATH}/Makefile
}

pkg_postinst_${PN} () {
	grep -q "^${base_bindir}/bash$" $D${sysconfdir}/shells || echo ${base_bindir}/bash >> $D${sysconfdir}/shells
}

pkg_postrm_${PN} () {
	printf "$(grep -v "^${base_bindir}/bash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}

PACKAGES += "${PN}-bashbug"
FILES_${PN} = "${bindir}/bash ${base_bindir}/bash.bash"
FILES_${PN}-bashbug = "${bindir}/bashbug"

PACKAGE_BEFORE_PN += "${PN}-loadable"
RDEPENDS_${PN}-loadable += "${PN}"
FILES_${PN}-loadable += "${libdir}/bash/*"

RPROVIDES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sh /bin/bash', '', d)}"
