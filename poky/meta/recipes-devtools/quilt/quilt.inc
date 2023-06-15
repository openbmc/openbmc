SUMMARY = "Tool for working with series of patches"
DESCRIPTION = "Quilt is a tool to manage large sets of patches by keeping \
track of the changes each patch makes. Patches can be applied, un-applied,\
refreshed, etc."
HOMEPAGE = "http://savannah.nongnu.org/projects/quilt/"
SECTION = "devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/quilt/quilt-${PV}.tar.gz \
        file://run-ptest \
        file://Makefile \
        file://test.sh \
        file://0001-tests-Allow-different-output-from-mv.patch \
        file://fix-grep-3.8.patch \
        file://faildiff-order.patch \
        file://0001-test-Fix-a-race-condition-in-merge.test.patch \
"

SRC_URI:append:class-target = " file://gnu_patch_test_fix_target.patch"

SRC_URI[sha256sum] = "3be3be0987e72a6c364678bb827e3e1fcc10322b56bc5f02b576698f55013cc2"

inherit autotools-brokensep ptest

INHIBIT_AUTOTOOLS_DEPS:class-native = "1"
PATCHTOOL:class-native = "patch"

CLEANBROKEN = "1"

EXTRA_OECONF = "--with-perl='${USRBINPATH}/env perl' --with-patch=patch --without-sendmail"
EXTRA_OECONF:append:class-native = " --disable-nls"

EXTRA_AUTORECONF += "--exclude=aclocal"

CACHED_CONFIGUREVARS += "ac_cv_path_BASH=/bin/bash ac_cv_path_COLUMN=column"

# Make sure we don't have "-w" in shebang lines: it breaks using
# "/usr/bin/env perl" as parser
do_configure:prepend () {
	find ${S} -name "*.in" -exec sed -i -e "1s,^#\!.*@PERL@ -w$,#\! @PERL@\nuse warnings;," {} \;
}

# Don't setup symlinks to host utilities, we don't need them
do_configure:append () {
	sed -e 's,^COMPAT_SYMLINKS.*:=.*,COMPAT_SYMLINKS	:=,' -i ${S}/Makefile
}

do_configure:class-native () {
    oe_runconf
}

# quilt Makefiles install to BUILD_ROOT instead of DESTDIR
do_install () {
	oe_runmake 'BUILD_ROOT=${D}' install
	# cleanup unpackaged files
	rm -rf ${D}/${datadir}/emacs
}

do_install:append:class-native () {
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

FILES:${PN} = "${sysconfdir} ${datadir}/quilt \
               ${bindir}/quilt ${libdir}/quilt"
FILES:guards = "${bindir}/guards"
FILES:${PN}-doc = "${mandir}/man1/quilt.1 ${docdir}/${BPN}"
FILES:guards-doc = "${mandir}/man1/guards.1"

RDEPENDS:${PN} = "bash patch diffstat bzip2 util-linux less"
RDEPENDS:${PN}:class-native = "diffstat-native patch-native bzip2-native"

RDEPENDS:${PN}-ptest = "make file sed gawk diffutils findutils ed perl \
                        perl-module-filehandle perl-module-getopt-std \
                        perl-module-posix perl-module-file-temp \
                        perl-module-text-parsewords perl-module-overloading \
                        bash util-linux-getopt patch \
                       "
