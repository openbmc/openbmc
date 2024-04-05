SUMMARY = "Flex (The Fast Lexical Analyzer)"
DESCRIPTION = "Flex is a fast lexical analyser generator.  Flex is a tool for generating programs that recognize \
lexical patterns in text."
HOMEPAGE = "http://sourceforge.net/projects/flex/"
SECTION = "devel"
LICENSE = "BSD-3-Clause & LGPL-2.0-or-later"
LICENSE:${PN}-libfl = "BSD-3-Clause"

DEPENDS = "${@bb.utils.contains('PTEST_ENABLED', '1', 'bison-native flex-native', '', d)}"
BBCLASSEXTEND = "native nativesdk"

LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067 \
                    file://src/gettext.h;beginline=1;endline=17;md5=9c05dda2f58d89b850c399cf22e1a00c"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/flex-${PV}.tar.gz \
           file://run-ptest \
           file://0001-tests-add-a-target-for-building-tests-without-runnin.patch \
           ${@bb.utils.contains('PTEST_ENABLED', '1', '', 'file://disable-tests.patch', d)} \
           file://0001-build-AC_USE_SYSTEM_EXTENSIONS-in-configure.ac.patch \
           file://check-funcs.patch \
           file://0001-Emit-no-line-directives-if-gen_line_dirs-is-false.patch \
           "

SRC_URI[md5sum] = "2882e3179748cc9f9c23ec593d6adc8d"
SRC_URI[sha256sum] = "e87aae032bf07c26f85ac0ed3250998c37621d95f8bd748b31f15b33c45ee995"

GITHUB_BASE_URI = "https://github.com/westes/flex/releases"

# https://github.com/westes/flex/issues/414
CVE_STATUS[CVE-2019-6293] = "upstream-wontfix: \
there is stack exhaustion but no bug and it is building the \
parser, not running it, effectively similar to a compiler ICE. Upstream no plans to address this."

inherit autotools gettext texinfo ptest github-releases

M4 = "${bindir}/m4"
M4:class-native = "${STAGING_BINDIR_NATIVE}/m4"
EXTRA_OECONF += "ac_cv_path_M4=${M4} ac_cv_func_reallocarray=no"
EXTRA_OEMAKE += "m4=${STAGING_BINDIR_NATIVE}/m4"

EXTRA_OEMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', 'FLEX=${STAGING_BINDIR_NATIVE}/flex', '', d)}"

do_install:append:class-native() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

do_install:append:class-nativesdk() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

PACKAGES =+ "${PN}-libfl"

FILES:${PN}-libfl = "${libdir}/libfl.so.* ${libdir}/libfl_pic.so.*"

RDEPENDS:${PN} += "m4"
RDEPENDS:${PN}-ptest += "bash gawk make"

do_compile_ptest() {
	oe_runmake -C ${B}/tests -f ${B}/tests/Makefile top_builddir=${B} INCLUDES=-I${S}/src buildtests
}
PTEST_PARALLEL_MAKE = ""

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}/build-aux/
	cp ${S}/build-aux/test-driver ${D}${PTEST_PATH}/build-aux/
	cp -r ${S}/tests/* ${D}${PTEST_PATH}
	cp -r ${B}/tests/* ${D}${PTEST_PATH}
	sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:\(^LDFLAGS_FOR_BUILD =\).*:\1:g' \
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
