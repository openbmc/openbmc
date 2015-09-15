SUMMARY = "Flex (The Fast Lexical Analyzer)"
DESCRIPTION = "Flex is a fast lexical analyser generator.  Flex is a tool for generating programs that recognize \
lexical patterns in text."
HOMEPAGE = "http://sourceforge.net/projects/flex/"

SECTION = "devel"
LICENSE = "BSD"
DEPENDS += "${@'bison-native flex-native' if '${PTEST_ENABLED}' == '1' else ''}"

SRC_URI = "${SOURCEFORGE_MIRROR}/flex/flex-${PV}.tar.bz2 \
           file://run-ptest \
"

inherit autotools gettext texinfo ptest


M4 = "${bindir}/m4"
M4_class-native = "${STAGING_BINDIR_NATIVE}/m4"

EXTRA_OECONF += "ac_cv_path_M4=${M4}"
EXTRA_OEMAKE += "m4=${STAGING_BINDIR_NATIVE}/m4"

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

RDEPENDS_${PN} += "m4"

do_compile_ptest() {
	for i in `find ${S}/tests/ -type d |grep -Ev "concatenated-options|reject|table-opts" | awk -F/ '{print $NF}'`; \
	  do oe_runmake -C ${S}/tests/$i -f ${B}/tests/$i/Makefile top_builddir=${B} FLEX=flex $i; \
	done
	oe_runmake -C ${S}/tests/test-reject -f ${B}/tests/test-reject/Makefile top_builddir=${B} FLEX=flex test-reject-nr test-reject-r test-reject-ser test-reject-ver
}

do_install_ptest() {
	for i in `find ${S}/tests/ -type d | awk -F/ '{print $NF}'`; \
	  do cp -r ${S}/tests/$i ${D}${PTEST_PATH}; \
	done
}
