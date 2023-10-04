require gzip.inc

# change to GPL-3.0-or-later in 2007/07. Previous GPL-2.0-or-later version is
# 1.3.12
LICENSE = "GPL-3.0-or-later"

SRC_URI = "${GNU_MIRROR}/gzip/${BP}.tar.gz \
           file://run-ptest \
           "
SRC_URI:append:class-target = " file://wrong-path-fix.patch"

LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e"

PROVIDES:append:class-native = " gzip-replacement-native"

RDEPENDS:${PN}-ptest += "make perl grep diffutils"

BBCLASSEXTEND = "native nativesdk"

inherit ptest

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}/src/build-aux
	cp ${S}/build-aux/test-driver ${D}${PTEST_PATH}/src/build-aux/
	mkdir -p ${D}${PTEST_PATH}/src/tests
	cp -r ${S}/tests/* ${D}${PTEST_PATH}/src/tests
	sed -e 's/^abs_srcdir = ..*/abs_srcdir = \.\./' \
            -e 's/^top_srcdir = ..*/top_srcdir = \.\./' \
            -e 's/^GREP = ..*/GREP = grep/'             \
            -e 's/^AWK = ..*/AWK = awk/'                \
            -e 's/^srcdir = ..*/srcdir = \./'           \
            -e 's/^Makefile: ..*/Makefile: /'           \
            -e 's,--sysroot=${STAGING_DIR_TARGET},,g'   \
            -e 's|${DEBUG_PREFIX_MAP}||g' \
            -e 's:${HOSTTOOLS_DIR}/::g'                 \
            -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
            ${B}/tests/Makefile > ${D}${PTEST_PATH}/src/tests/Makefile
}

SRC_URI[sha256sum] = "20fc818aeebae87cdbf209d35141ad9d3cf312b35a5e6be61bfcfbf9eddd212a"
