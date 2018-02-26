SUMMARY = "Flex (The Fast Lexical Analyzer)"
DESCRIPTION = "Flex is a fast lexical analyser generator.  Flex is a tool for generating programs that recognize \
lexical patterns in text."
HOMEPAGE = "http://sourceforge.net/projects/flex/"
SECTION = "devel"
LICENSE = "BSD"

DEPENDS = "${@bb.utils.contains('PTEST_ENABLED', '1', 'bison-native flex-native', '', d)}"
BBCLASSEXTEND = "native nativesdk"

LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067"

SRC_URI = "${SOURCEFORGE_MIRROR}/flex/flex-${PV}.tar.bz2 \
           file://run-ptest \
           file://do_not_create_pdf_doc.patch \
           file://0001-tests-add-a-target-for-building-tests-without-runnin.patch \
           file://0002-avoid-c-comments-in-c-code-fails-with-gcc-6.patch \
           file://CVE-2016-6354.patch \
           ${@bb.utils.contains('PTEST_ENABLED', '1', '', 'file://disable-tests.patch', d)} \
           "

SRC_URI[md5sum] = "266270f13c48ed043d95648075084d59"
SRC_URI[sha256sum] = "24e611ef5a4703a191012f80c1027dc9d12555183ce0ecd46f3636e587e9b8e9"

# Flex has moved to github from 2.6.1 onwards
UPSTREAM_CHECK_URI = "https://github.com/westes/flex/releases"
UPSTREAM_CHECK_REGEX = "flex-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools gettext texinfo ptest

M4 = "${bindir}/m4"
M4_class-native = "${STAGING_BINDIR_NATIVE}/m4"
EXTRA_OECONF += "ac_cv_path_M4=${M4}"
EXTRA_OEMAKE += "m4=${STAGING_BINDIR_NATIVE}/m4"

EXTRA_OEMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', 'FLEX=${STAGING_BINDIR_NATIVE}/flex', '', d)}"

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

RDEPENDS_${PN} += "m4"
RDEPENDS_${PN}-ptest += "bash gawk"

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
	    -i ${D}${PTEST_PATH}/Makefile
}
