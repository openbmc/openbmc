LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

require diffutils.inc

SRC_URI = "${GNU_MIRROR}/diffutils/diffutils-${PV}.tar.xz \
           file://run-ptest \
           file://0001-Skip-strip-trailing-cr-test-case.patch \
           "

SRC_URI[sha256sum] = "a6bdd7d1b31266d11c4f4de6c1b748d4607ab0231af5188fc2533d0ae2438fec"

EXTRA_OECONF += "ac_cv_path_PR_PROGRAM=${bindir}/pr --without-libsigsegv-prefix"

# latest gnulib is no longer able to handle this - I dare not try to fix that maze of abstractions and generators
CFLAGS:mingw32 = " -DSA_RESTART=0"

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

inherit ptest

RDEPENDS:${PN}-ptest += "make perl"

do_install_ptest() {
	t=${D}${PTEST_PATH}
	install -D ${S}/build-aux/test-driver $t/build-aux/test-driver
	cp -r ${S}/tests $t/
	install ${B}/tests/Makefile $t/tests/
	sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
	    -e 's|^Makefile:|_Makefile:|' \
	    -e 's|bash|sh|' \
	    -e 's|^top_srcdir = \(.*\)|top_srcdir = ..\/|' \
	    -e 's|^srcdir = \(.*\)|srcdir = .|' \
	    -e 's|"`$(built_programs)`"|diff|' \
	    -e 's|gawk|awk|g' \
	    -i $t/tests/Makefile
}
