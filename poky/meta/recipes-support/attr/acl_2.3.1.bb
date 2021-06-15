SUMMARY = "Utilities for managing POSIX Access Control Lists"
DESCRIPTION = "ACL allows you to provide different levels of access to files \
and folders for different users."

HOMEPAGE = "http://savannah.nongnu.org/projects/acl/"
BUGTRACKER = "http://savannah.nongnu.org/bugs/?group=acl"

SECTION = "libs"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=c781d70ed2b4d48995b790403217a249 \
                    file://doc/COPYING.LGPL;md5=9e9a206917f8af112da634ce3ab41764"

DEPENDS = "attr"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/acl/${BP}.tar.gz \
           file://run-ptest \
           file://0001-tests-do-not-hardcode-the-build-path-into-a-helper-l.patch \
           file://0001-test-patch-out-failing-bits.patch \
           "

SRC_URI[sha256sum] = "760c61c68901b37fdd5eefeeaf4c0c7a26bdfdd8ac747a1edff1ce0e243c11af"

inherit autotools gettext ptest

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${libdir}/lib*${SOLIBS}"

PTEST_BUILD_HOST_FILES = "builddefs"
PTEST_BUILD_HOST_PATTERN = "^RPM"

do_compile_ptest() {
        oe_runmake libtestlookup.la
}

do_install_ptest() {
	cp -rf ${S}/test/ ${D}${PTEST_PATH}
	cp -rf ${S}/build-aux/ ${D}${PTEST_PATH}
        mkdir -p ${D}${PTEST_PATH}/.libs
	cp -rf ${B}/.libs/libtestlookup* ${D}${PTEST_PATH}/.libs
        cp ${B}/Makefile ${D}${PTEST_PATH}

        sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
            -e 's|${DEBUG_PREFIX_MAP}||g' \
            -e 's:${HOSTTOOLS_DIR}/::g' \
            -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
            -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
            -i ${D}${PTEST_PATH}/Makefile

        sed -i "s|^srcdir =.*|srcdir = \.|g" ${D}${PTEST_PATH}/Makefile
        sed -i "s|^abs_srcdir =.*|abs_srcdir = \.|g" ${D}${PTEST_PATH}/Makefile
        sed -i "s|^abs_top_srcdir =.*|abs_top_srcdir = \.\.|g" ${D}${PTEST_PATH}/Makefile
        sed -i "s|^Makefile:.*|Makefile:|g" ${D}${PTEST_PATH}/Makefile

        rm ${D}${PTEST_PATH}/.libs/libtestlookup.lai
}

RDEPENDS_${PN}-ptest = "acl \
                        bash \
                        coreutils \
                        perl \
                        perl-module-filehandle \
                        perl-module-getopt-std \
                        perl-module-posix \
                        shadow \
                        make \
                        gawk \
                        e2fsprogs-mke2fs \
                        perl-module-cwd \
                        perl-module-file-basename \
                        perl-module-file-path \
                        perl-module-file-spec \
                       "

BBCLASSEXTEND = "native nativesdk"
