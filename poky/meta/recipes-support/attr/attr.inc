SUMMARY = "Utilities for manipulating filesystem extended attributes"
DESCRIPTION = "A set of tools for manipulating extended attributes on filesystem \
objects, in particular getfattr(1) and setfattr(1). An attr(1) command \
is also provided which is largely compatible with the SGI IRIX tool of \
the same name."
HOMEPAGE = "http://savannah.nongnu.org/projects/attr/"
SECTION = "libs"

DEPENDS = "virtual/libintl"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=2d0aa14b3fce4694e4f615e30186335f \
                    file://doc/COPYING.LGPL;md5=b8d31f339300bc239d73461d68e77b9c \
                    file://tools/attr.c;endline=17;md5=be0403261f0847e5f43ed5b08d19593c \
                    file://libattr/libattr.c;endline=17;md5=7970f77049f8fa1199fff62a7ab724fb"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/attr/${BP}.tar.gz \
           file://run-ptest \
"

inherit ptest update-alternatives autotools gettext

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${libdir}/lib*${SOLIBS}"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "setfattr"
ALTERNATIVE_TARGET[setfattr] = "${bindir}/setfattr"

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

        sed -i "s|^srcdir =.*|srcdir = \.|g" ${D}${PTEST_PATH}/Makefile
        sed -i "s|^abs_srcdir =.*|abs_srcdir = \.|g" ${D}${PTEST_PATH}/Makefile
        sed -i "s|^abs_top_srcdir =.*|abs_top_srcdir = \.\.|g" ${D}${PTEST_PATH}/Makefile
        sed -i "s|^Makefile:.*|Makefile:|g" ${D}${PTEST_PATH}/Makefile
	cp -rf ${S}/build-aux/ ${D}${PTEST_PATH}
	cp -rf ${S}/test/ ${D}${PTEST_PATH}
}

RDEPENDS_${PN}-ptest = "attr \
                        coreutils \
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
