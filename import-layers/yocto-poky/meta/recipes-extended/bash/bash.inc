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

ALTERNATIVE_${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/bash"
ALTERNATIVE_PRIORITY = "100"

RDEPENDS_${PN} += "base-files"
RDEPENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}-ptest += "make"

USERADD_PACKAGES = "${PN}-ptest"
USERADD_PARAM_${PN}-ptest = "--create-home --user-group test"

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
	# Clean host path in bashbug
	sed -i -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
		-e "s,-I${WORKDIR}/\S* ,,g" ${D}${bindir}/bashbug
}

do_install_ptest () {
	make INSTALL_TEST_DIR=${D}${PTEST_PATH}/tests install-test
	cp ${B}/Makefile ${D}${PTEST_PATH}
        sed -i -e 's/^Makefile/_Makefile/' -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
	    -e "s,${S},,g" -e "s,${B},,g" -e "s,${STAGING_DIR_NATIVE},,g" ${D}${PTEST_PATH}/Makefile
}

pkg_postinst_${PN} () {
	grep -q "^${base_bindir}/bash$" $D${sysconfdir}/shells || echo ${base_bindir}/bash >> $D${sysconfdir}/shells
}

pkg_postrm_${PN} () {
	printf "$(grep -v "^${base_bindir}/bash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
