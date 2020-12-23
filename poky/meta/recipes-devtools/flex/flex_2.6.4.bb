SUMMARY = "Flex (The Fast Lexical Analyzer)"
DESCRIPTION = "Flex is a fast lexical analyser generator.  Flex is a tool for generating programs that recognize \
lexical patterns in text."
HOMEPAGE = "http://sourceforge.net/projects/flex/"
SECTION = "devel"
LICENSE = "BSD-2-Clause"

DEPENDS = "${@bb.utils.contains('PTEST_ENABLED', '1', 'bison-native flex-native', '', d)}"
BBCLASSEXTEND = "native nativesdk"

LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067"

SRC_URI = "https://github.com/westes/flex/releases/download/v${PV}/flex-${PV}.tar.gz \
           file://run-ptest \
           file://0001-tests-add-a-target-for-building-tests-without-runnin.patch \
           ${@bb.utils.contains('PTEST_ENABLED', '1', '', 'file://disable-tests.patch', d)} \
           file://0001-build-AC_USE_SYSTEM_EXTENSIONS-in-configure.ac.patch \
           file://check-funcs.patch \
           "

SRC_URI[md5sum] = "2882e3179748cc9f9c23ec593d6adc8d"
SRC_URI[sha256sum] = "e87aae032bf07c26f85ac0ed3250998c37621d95f8bd748b31f15b33c45ee995"

# Flex has moved to github from 2.6.1 onwards
UPSTREAM_CHECK_URI = "https://github.com/westes/flex/releases"
UPSTREAM_CHECK_REGEX = "flex-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools gettext texinfo ptest

M4 = "${bindir}/m4"
M4_class-native = "${STAGING_BINDIR_NATIVE}/m4"
EXTRA_OECONF += "ac_cv_path_M4=${M4} ac_cv_func_reallocarray=no"
EXTRA_OEMAKE += "m4=${STAGING_BINDIR_NATIVE}/m4"

EXTRA_OEMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', 'FLEX=${STAGING_BINDIR_NATIVE}/flex', '', d)}"

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

PACKAGES =+ "${PN}-libfl"

FILES_${PN}-libfl = "${libdir}/libfl.so.* ${libdir}/libfl_pic.so.*"

RDEPENDS_${PN} += "m4"
RDEPENDS_${PN}-ptest += "bash gawk make"

do_compile_ptest() {
	oe_runmake -C ${B}/tests -f ${B}/tests/Makefile top_builddir=${B} INCLUDES=-I${S}/src buildtests
}

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}/build-aux/
	cp ${S}/build-aux/test-driver ${D}${PTEST_PATH}/build-aux/
	cp -r ${S}/tests/* ${D}${PTEST_PATH}
	cp -r ${B}/tests/* ${D}${PTEST_PATH}
	sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \-e 's/^Makefile:/_Makefile:/' \
	    -e 's/^srcdir = \(.*\)/srcdir = ./' -e 's/^top_srcdir = \(.*\)/top_srcdir = ./' \
	    -e 's/^builddir = \(.*\)/builddir = ./' -e 's/^top_builddir = \(.*\)/top_builddir = ./' \
	    -e 's:${UNINATIVE_LOADER}:${base_bindir}/false:g' \
	    -i ${D}${PTEST_PATH}/Makefile
}
# The uninative loader is different on i386 & x86_64 hosts. Since it is only
# being replaced with /bin/false anyway, it doesn't need to be part of the task
# hash
do_install_ptest[vardepsexclude] += "UNINATIVE_LOADER"

# Not Apache Flex, or Adobe Flex, or IBM Flex.
CVE_PRODUCT = "flex_project:flex"
