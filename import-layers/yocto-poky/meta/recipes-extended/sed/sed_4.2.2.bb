SUMMARY = "Stream EDitor (text filtering utility)"
HOMEPAGE = "http://www.gnu.org/software/sed/"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://sed/sed.h;beginline=1;endline=17;md5=767ab3a06d7584f6fd0469abaec4412f"
SECTION = "console/utils"

SRC_URI = "${GNU_MIRROR}/sed/sed-${PV}.tar.gz \
           file://sed-add-ptest.patch \
	   file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://run-ptest \
"

SRC_URI[md5sum] = "4111de4faa3b9848a0686b2f260c5056"
SRC_URI[sha256sum] = "fea0a94d4b605894f3e2d5572e3f96e4413bcad3a085aae7367c2cf07908b2ff"

inherit autotools texinfo update-alternatives gettext ptest
RDEPENDS_${PN}-ptest += "make ${PN}"
RRECOMMENDS_${PN}-ptest_append_libc-glibc = " locale-base-ru-ru"

EXTRA_OECONF = "--disable-acl \
                ${@bb.utils.contains('PTEST_ENABLED', '1', '--enable-regex-tests', '', d)}"

do_install () {
	autotools_do_install
	install -d ${D}${base_bindir}
	if [ ! ${D}${bindir} -ef ${D}${base_bindir} ]; then
	    mv ${D}${bindir}/sed ${D}${base_bindir}/sed
	    rmdir ${D}${bindir}/
	fi
}

ALTERNATIVE_${PN} = "sed"
ALTERNATIVE_LINK_NAME[sed] = "${base_bindir}/sed"
ALTERNATIVE_PRIORITY = "100"

TESTDIR = "testsuite"

do_compile_ptest() {
	oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
	sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
	    -i ${D}${PTEST_PATH}/${TESTDIR}/Makefile
}

RPROVIDES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sed', '', d)}"
