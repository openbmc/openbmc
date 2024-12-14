SUMMARY = "Utilities for manipulating filesystem extended attributes"
DESCRIPTION = "Implement the ability for a user to attach name:value pairs to objects within the XFS filesystem."

HOMEPAGE = "http://savannah.nongnu.org/projects/attr/"
SECTION = "libs"

DEPENDS = "virtual/libintl"

LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:lib${BPN} = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=2d0aa14b3fce4694e4f615e30186335f \
                    file://doc/COPYING.LGPL;md5=b8d31f339300bc239d73461d68e77b9c \
                    file://tools/attr.c;endline=17;md5=be0403261f0847e5f43ed5b08d19593c \
                    file://libattr/libattr.c;endline=17;md5=7970f77049f8fa1199fff62a7ab724fb"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/attr/${BP}.tar.gz \
           file://run-ptest \
           file://0001-attr.c-Include-libgen.h-for-posix-version-of-basenam.patch \
"

inherit ptest update-alternatives autotools gettext

PACKAGES =+ "lib${BPN}"

FILES:lib${BPN} = "${libdir}/lib*${SOLIBS} ${sysconfdir}"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "setfattr getfattr"
ALTERNATIVE_TARGET[setfattr] = "${bindir}/setfattr"
ALTERNATIVE_TARGET[getfattr] = "${bindir}/getfattr"

PTEST_BUILD_HOST_FILES = "builddefs"
PTEST_BUILD_HOST_PATTERN = "^RPM"

do_install_ptest() {
        cp ${B}/Makefile ${D}${PTEST_PATH}
        sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
            -e 's|${DEBUG_PREFIX_MAP}||g' \
            -e 's:${HOSTTOOLS_DIR}/::g' \
            -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
            -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
            -i ${D}${PTEST_PATH}/Makefile

	sed -e "s|^srcdir =.*|srcdir = .|" \
	    -e "s|^abs_srcdir =.*|abs_srcdir = .|" \
	    -e "s|^abs_top_srcdir =.*|abs_top_srcdir = ..|" \
	    -e "s|^Makefile:.*|Makefile:|" \
	    -e "/^TEST_LOG_DRIVER =/s|(top_srcdir)|(top_builddir)|" \
	    -i ${D}${PTEST_PATH}/Makefile

	cp -rf ${S}/build-aux/ ${D}${PTEST_PATH}
	cp -rf ${S}/test/ ${D}${PTEST_PATH}
}

do_install_ptest:append:libc-musl() {
        sed -i -e 's|f: Operation n|f: N|g' ${D}${PTEST_PATH}/test/attr.test
}

RDEPENDS:${PN}-ptest = "attr \
                        bash \
                        coreutils \
                        perl-module-constant \
                        perl-module-filehandle \
                        perl-module-getopt-std \
                        perl-module-posix \
                        make \
                        perl \
                        gawk \
                        perl-module-cwd \
                        perl-module-file-basename \
                        perl-module-file-path \
                        perl-module-file-spec \
                        "

BBCLASSEXTEND = "native nativesdk"
