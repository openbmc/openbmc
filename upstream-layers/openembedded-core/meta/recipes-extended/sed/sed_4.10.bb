SUMMARY = "Stream EDitor (text filtering utility)"
HOMEPAGE = "http://www.gnu.org/software/sed/"
DESCRIPTION = "sed (stream editor) is a non-interactive command-line text editor."
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://sed/sed.h;beginline=1;endline=15;md5=3baf8c344ffa6281a98c30294ac62a6c \
                    "
SECTION = "console/utils"

SRC_URI = "${GNU_MIRROR}/sed/sed-${PV}.tar.xz \
           file://run-ptest \
"

SRC_URI[sha256sum] = "b8e72182b2ec96a3574e2998c47b7aaa64cc20ce000d8e9ac313cc07cecf28c7"

inherit autotools texinfo update-alternatives gettext ptest

PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

RDEPENDS:${PN}-ptest += "make gawk perl perl-module-filehandle perl-module-file-compare perl-module-file-find perl-module-file-temp perl-module-file-stat"
RRECOMMENDS:${PN}-ptest:append:libc-glibc = " locale-base-ru-ru locale-base-en-us locale-base-el-gr.iso-8859-7"

EXTRA_OECONF = "--disable-acl \
               "

do_install () {
	autotools_do_install
	install -d ${D}${base_bindir}
	if [ ! ${D}${bindir} -ef ${D}${base_bindir} ]; then
	    mv ${D}${bindir}/sed ${D}${base_bindir}/sed
	    rmdir ${D}${bindir}/
	fi
}

ALTERNATIVE:${PN} = "sed"
ALTERNATIVE_LINK_NAME[sed] = "${base_bindir}/sed"
ALTERNATIVE_PRIORITY = "100"

do_compile_ptest() {
	oe_runmake testsuite/get-mb-cur-max testsuite/test-mbrtowc
}

do_install_ptest() {
        cp -rf ${S}/testsuite/ ${D}${PTEST_PATH}
        cp -rf ${B}/testsuite/* ${D}${PTEST_PATH}/testsuite/
        cp -rf ${S}/build-aux/ ${D}${PTEST_PATH}/
        cp ${B}/Makefile ${D}${PTEST_PATH}
        cp ${S}/init.cfg ${D}${PTEST_PATH}

        sed -e 's/^Makefile:/_Makefile:/' -e 's/^srcdir = \(.*\)/srcdir = ./' -e 's/bash/sh/' -i ${D}${PTEST_PATH}/Makefile
        for i in `grep -rl "sed/sed" ${D}${PTEST_PATH}`; do sed -e 's/..\/sed\/sed/sed/' -i $i; done

	sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -e 's:abs_top_builddir =.*:abs_top_builddir = ..:g' \
	    -e 's:abs_top_srcdir =.*:abs_top_srcdir = ..:g' \
	    -e 's:abs_srcdir =.*:abs_srcdir = ..:g' \
	    -e 's:top_srcdir =.*:top_srcdir = ..:g' \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
	    -e "/^\(PL\|SH\)_LOG_DRIVER =/s|(top_srcdir)|(top_builddir)|" \
	    -i ${D}${PTEST_PATH}/Makefile
}

RPROVIDES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sed', '', d)}"

BBCLASSEXTEND = "nativesdk"
