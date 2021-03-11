SUMMARY = "Tool for working with series of patches"
DESCRIPTION = "Quilt is a tool to manage large sets of patches by keeping \
track of the changes each patch makes. Patches can be applied, un-applied,\
refreshed, etc."
HOMEPAGE = "http://savannah.nongnu.org/projects/quilt/"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/quilt/quilt-${PV}.tar.gz \
        file://run-ptest \
        file://Makefile \
        file://test.sh \
        file://0001-tests-Allow-different-output-from-mv.patch \
"

SRC_URI_append_class-target = " file://gnu_patch_test_fix_target.patch"

SRC_URI[md5sum] = "6800c2404a2c0598ab2eff92a636ba70"
SRC_URI[sha256sum] = "314b319a6feb13bf9d0f9ffa7ce6683b06919e734a41275087ea457cc9dc6e07"

inherit autotools-brokensep ptest

INHIBIT_AUTOTOOLS_DEPS_class-native = "1"
PATCHTOOL_class-native = "patch"

CLEANBROKEN = "1"

EXTRA_OECONF = "--with-perl='${USRBINPATH}/env perl' --with-patch=patch"
EXTRA_OECONF_append_class-native = " --disable-nls"
EXTRA_AUTORECONF += "--exclude=aclocal"

CACHED_CONFIGUREVARS += "ac_cv_path_BASH=/bin/bash ac_cv_path_COLUMN=column"

# Make sure we don't have "-w" in shebang lines: it breaks using
# "/usr/bin/env perl" as parser
do_configure_prepend () {
	find ${S} -name "*.in" -exec sed -i -e "1s,^#\!.*@PERL@ -w$,#\! @PERL@\nuse warnings;," {} \;
}

# Don't setup symlinks to host utilities, we don't need them
do_configure_append () {
	sed -e 's,^COMPAT_SYMLINKS.*:=.*,COMPAT_SYMLINKS	:=,' -i ${S}/Makefile
}

do_configure_class-native () {
    oe_runconf
}

# quilt Makefiles install to BUILD_ROOT instead of DESTDIR
do_install () {
	oe_runmake 'BUILD_ROOT=${D}' install
	# cleanup unpackaged files
	rm -rf ${D}/${datadir}/emacs
}

do_install_append_class-native () {
    # Dummy quiltrc file for patch.bbclass
    install -d ${D}${sysconfdir}/
    touch ${D}${sysconfdir}/quiltrc
}

do_compile_ptest() {
	oe_runmake bin/patch-wrapper test/.depend
}

do_install_ptest() {
	tar -c --exclude=\*.in bin/ | ( cd ${D}${PTEST_PATH} && tar -xf - )
	tar -c --exclude=\*.in compat/ | ( cd ${D}${PTEST_PATH} && tar -xf - )
	tar -c --exclude=\*.in quilt/ | ( cd ${D}${PTEST_PATH} && tar -xf - )
	tar -c --exclude=mail.test --exclude=delete.test test/ | ( cd ${D}${PTEST_PATH} && tar -xf - && chmod 777 test)
	cp ${WORKDIR}/Makefile ${D}${PTEST_PATH}
	cp ${WORKDIR}/test.sh ${D}${PTEST_PATH}
}

PACKAGES += "guards guards-doc"

FILES_${PN} = "${sysconfdir} ${datadir}/quilt \
               ${bindir}/quilt ${libdir}/quilt"
FILES_guards = "${bindir}/guards"
FILES_${PN}-doc = "${mandir}/man1/quilt.1 ${docdir}/${BPN}"
FILES_guards-doc = "${mandir}/man1/guards.1"

RDEPENDS_${PN} = "bash patch diffstat bzip2 util-linux less"
RDEPENDS_${PN}_class-native = "diffstat-native patch-native bzip2-native"

RDEPENDS_${PN}-ptest = "make file sed gawk diffutils findutils ed perl \
                        perl-module-filehandle perl-module-getopt-std \
                        perl-module-posix perl-module-file-temp \
                        perl-module-text-parsewords perl-module-overloading \
                        bash util-linux-getopt patch \
                       "
