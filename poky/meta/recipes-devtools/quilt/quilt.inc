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
        file://test.sh \
        file://0001-tests-Allow-different-output-from-mv.patch \
"

SRC_URI:append:class-target = " file://gnu_patch_test_fix_target.patch"

SRC_URI[sha256sum] = "fe8c09de03c106e85b3737c8f03ade147c956b79ed7af485a1c8a3858db38426"

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

# The tests need to run as a non-root user, so pull in the ptest user
DEPENDS:append:class-target = "${@bb.utils.contains('PTEST_ENABLED', '1', ' ptest-runner', '', d)}"
PACKAGE_WRITE_DEPS += "ptest-runner"

do_install_ptest() {
	install ${UNPACKDIR}/test.sh ${D}${PTEST_PATH}
    mkdir ${D}${PTEST_PATH}/test
    install ${S}/test/* ${D}${PTEST_PATH}/test
    # mail needs a MTA, and the patch-wrapper is disabled
    rm -f ${D}${PTEST_PATH}/test/mail.test ${D}${PTEST_PATH}/test/patch-wrapper.test
}

# ptest.bbclass currently chowns the ptest directory explicitly, so we need to
# change permission after that has happened so the ptest user can write a
# temporary directory.
do_install_ptest_base:append() {
    chgrp ptest ${D}${PTEST_PATH}/test
    chmod g+w ${D}${PTEST_PATH}/test
}

PACKAGES += "guards guards-doc"

FILES:${PN} = "${sysconfdir} ${datadir}/quilt \
               ${bindir}/quilt ${libdir}/quilt"
FILES:guards = "${bindir}/guards"
FILES:${PN}-doc = "${mandir}/man1/quilt.1 ${docdir}/${BPN}"
FILES:guards-doc = "${mandir}/man1/guards.1"

RDEPENDS:${PN} = "bash patch diffstat bzip2 util-linux less"
RDEPENDS:${PN}:class-native = "diffstat-native patch-native bzip2-native"

RDEPENDS:${PN}-ptest += "file sed gawk diffutils findutils ed perl \
                         perl-module-filehandle perl-module-getopt-std \
                         perl-module-posix perl-module-file-temp \
                         perl-module-text-parsewords perl-module-overloading \
                         bash util-linux-getopt ptest-runner coreutils \
                        "
