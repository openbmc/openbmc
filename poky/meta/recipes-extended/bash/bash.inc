SUMMARY = "An sh-compatible command language interpreter"
HOMEPAGE = "http://tiswww.case.edu/php/chet/bash/bashtop.html"
DESCRIPTION = "Bash is the GNU Project's Bourne Again SHell, a complete implementation of the IEEE POSIX and Open Group shell specification with interactive command line editing, job control on architectures that support it, csh-like features such as history substitution and brace expansion, and a slew of other features."
SECTION = "base/shell"

DEPENDS = "ncurses bison-native virtual/libiconv"

inherit autotools gettext texinfo update-alternatives ptest

EXTRA_AUTORECONF += "--exclude=autoheader"
EXTRA_OECONF = "--enable-job-control --without-bash-malloc bash_cv_wexitstatus_offset=8"

# If NON_INTERACTIVE_LOGIN_SHELLS is defined, all login shells read the
# startup files, even if they are not interactive.
# This is what other major distros do. And this is what we wanted. See bug#5359 and bug#7137.
CFLAGS += "-DNON_INTERACTIVE_LOGIN_SHELLS"

# This can vary depending upon the host
CFLAGS += "-DHEREDOC_PIPESIZE=65536"

# Disable bracketed paste mode by default (enabled by default in bash 5.1). It
# causes a lot of garbage in non-interactive shells
CFLAGS += "-DBRACKETED_PASTE_DEFAULT=0"

ALTERNATIVE:${PN} = "bash sh"
ALTERNATIVE_LINK_NAME[bash] = "${base_bindir}/bash"
ALTERNATIVE_TARGET[bash] = "${base_bindir}/bash"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/bash.${BPN}"
ALTERNATIVE_PRIORITY = "100"

RDEPENDS:${PN} += "base-files"
RDEPENDS:${PN}:class-nativesdk = ""
RDEPENDS:${PN}-ptest += "make coreutils perl sed shadow util-linux-setpriv"

RDEPENDS:${PN}-ptest:append:libc-glibc = " \
	glibc-gconv-big5hkscs \
	glibc-gconv-iso8859-1 \
	glibc-utils \
	locale-base-de-de \
	locale-base-en-us \
	locale-base-fr-fr \
	locale-base-fr-fr.iso-8859-1 \
	locale-base-zh-hk.big5-hkscs \
	"

CACHED_CONFIGUREVARS += "headersdir=${includedir}/${PN}"

do_configure:prepend () {
    if [ ! -e ${S}/acinclude.m4 ]; then
       cat ${S}/aclocal.m4 > ${S}/acinclude.m4
    fi
}

do_compile:prepend() {
    # Remove any leftover .build files. This ensures that bash always has the
    # same version number and keeps builds reproducible
    rm -f ${B}/.build
}

do_compile_ptest () {
	oe_runmake buildtest
}

do_install:prepend () {
	# Ensure determinism as this counter increases for each make call
	rm -f ${B}/.build
}

do_install:append () {
	# Move /usr/bin/bash to /bin/bash, if need
	if [ "${base_bindir}" != "${bindir}" ]; then
		mkdir -p ${D}${base_bindir}
		mv ${D}${bindir}/bash ${D}${base_bindir}
	fi
}

fix_absolute_paths () {
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

do_install:append:class-target () {
	fix_absolute_paths
}

do_install:append:class-nativesdk () {
	fix_absolute_paths
}

do_install_ptest () {
	make INSTALL_TEST_DIR=${D}${PTEST_PATH}/tests install-test
	cp ${B}/Makefile ${D}${PTEST_PATH}
	cp ${B}/config.h ${D}${PTEST_PATH}
	cp ${B}/version.h ${D}${PTEST_PATH}
	cp ${S}/y.tab.[ch] ${D}${PTEST_PATH}
	install -D ${WORKDIR}/run-bash-ptests ${D}${PTEST_PATH}/run-bash-ptests
        sed -i -e 's/^Makefile/_Makefile/' -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's|${BUILD_LDFLAGS}||g' \
	    -e "s,${S},,g" -e "s,${B},,g" -e "s,${STAGING_DIR_NATIVE},,g" \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${UNINATIVE_LOADER}:${base_bindir}/false:g' \
	     ${D}${PTEST_PATH}/Makefile
}
# The uninative loader is different on i386 & x86_64 hosts. Since it is only
# being replaced with /bin/false anyway, it doesn't need to be part of the task
# hash
do_install_ptest[vardepsexclude] += "UNINATIVE_LOADER"

pkg_postinst:${PN} () {
	grep -q "^${base_bindir}/bash$" $D${sysconfdir}/shells || echo ${base_bindir}/bash >> $D${sysconfdir}/shells
}

pkg_postrm:${PN} () {
	printf "$(grep -v "^${base_bindir}/bash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}

PACKAGES += "${PN}-bashbug"
FILES:${PN} = "${bindir}/bash ${base_bindir}/bash.bash"
FILES:${PN}-bashbug = "${bindir}/bashbug"

PACKAGE_BEFORE_PN += "${PN}-loadable"
RDEPENDS:${PN}-loadable += "${PN}"
FILES:${PN}-loadable += "${libdir}/bash/*"

# Limit the RPROVIDES here to class target so that if usrmerge is enabled for nativesdk, it does not
# include host system paths in /bin/
RPROVIDES:${PN}:append:class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sh /bin/bash', '', d)}"
