require findutils.inc

# GPL-2.0-or-later (<< 4.2.32), GPL-3.0-or-later (>= 4.2.32)
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = "bison-native"

SRC_URI[sha256sum] = "1387e0b67ff247d2abde998f90dfbf70c1491391a59ddfecb8ae698789f0a4f5"

PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"
# http://savannah.gnu.org/bugs/?27299
CACHED_CONFIGUREVARS += "gl_cv_func_wcwidth_works=yes"

EXTRA_OECONF += "ac_cv_path_SORT=${bindir}/sort"

# need od from coreutils for -t option
RDEPENDS:${PN}-ptest += "bash sed grep coreutils coreutils-getlimits"

do_install_ptest:class-target() {
	mkdir -p ${D}${PTEST_PATH}/tests/
	cp ${S}/init.cfg ${D}${PTEST_PATH}
	cp -r ${S}/tests/* ${D}${PTEST_PATH}/tests/

	# substitute value in run-ptest with actual version
	sed -i -e 's/__run_ptest_version__/${PV}/' ${D}${PTEST_PATH}/run-ptest
}
