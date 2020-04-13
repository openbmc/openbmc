require gcc-configure-common.inc

LICENSE = "NCSA | MIT"

LIC_FILES_CHKSUM = "\
    file://libsanitizer/LICENSE.TXT;md5=0249c37748936faf5b1efd5789587909 \
"

EXTRA_OECONF_PATHS = "\
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

do_configure () {
    rm -rf ${B}/${TARGET_SYS}/libsanitizer/
    mkdir -p ${B}/${TARGET_SYS}/libsanitizer/
    cd ${B}/${TARGET_SYS}/libsanitizer/
    chmod a+x ${S}/libsanitizer/configure
    relpath=${@os.path.relpath("${S}/libsanitizer", "${B}/${TARGET_SYS}/libsanitizer")}
    $relpath/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
    # Easiest way to stop bad RPATHs getting into the library since we have a
    # broken libtool here
    sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${B}/${TARGET_SYS}/libsanitizer/libtool
    # Link to the sysroot's libstdc++ instead of one gcc thinks it just built
    sed -i -e '/LIBSTDCXX_RAW_CXX_\(CXXFLAGS\|LDFLAGS\)\s*=/d' ${B}/${TARGET_SYS}/libsanitizer/*/Makefile
}
EXTRACONFFUNCS += "extract_stashed_builddir"
do_configure[depends] += "${COMPILERDEP}"

do_compile () {
    cd ${B}/${TARGET_SYS}/libsanitizer/
    oe_runmake MULTIBUILDTOP=${B}/${TARGET_SYS}/libsanitizer/
}

do_install () {
    cd ${B}/${TARGET_SYS}/libsanitizer/
    oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/${TARGET_SYS}/libsanitizer/ install
    if [ -d ${D}${infodir} ]; then
        rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
    fi
    chown -R root:root ${D}
}

INHIBIT_DEFAULT_DEPS = "1"
ALLOW_EMPTY_${PN} = "1"
DEPENDS = "virtual/crypt gcc-runtime virtual/${TARGET_PREFIX}gcc"

# used to fix ../../../../../../../../../work-shared/gcc-8.3.0-r0/gcc-8.3.0/libsanitizer/libbacktrace/../../libbacktrace/elf.c:772:21: error: 'st.st_mode' may be used uninitialized in this function [-Werror=maybe-uninitialized]
DEBUG_OPTIMIZATION_append = " -Wno-error"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "${PN} ${PN}-dbg"
PACKAGES += "libasan libubsan liblsan libtsan"
PACKAGES += "libasan-dev libubsan-dev liblsan-dev libtsan-dev"
PACKAGES += "libasan-staticdev libubsan-staticdev liblsan-staticdev libtsan-staticdev"

RDEPENDS_libasan += "libstdc++"
RDEPENDS_libubsan += "libstdc++"
RDEPENDS_liblsan += "libstdc++"
RDEPENDS_libtsan += "libstdc++"
RDEPENDS_libasan-dev += "${PN}"
RDEPENDS_libubsan-dev += "${PN}"
RDEPENDS_liblsan-dev += "${PN}"
RDEPENDS_libtsan-dev += "${PN}"
RRECOMMENDS_${PN} += "libasan libubsan"
RRECOMMENDS_${PN}_append_x86 = " liblsan"
RRECOMMENDS_${PN}_append_x86-64 = " liblsan libtsan"
RRECOMMENDS_${PN}_append_powerpc64 = " liblsan libtsan"
RRECOMMENDS_${PN}_append_aarch64 = " liblsan libtsan"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata"

# Only x86, powerpc, sparc, s390, arm, and aarch64 are supported
COMPATIBLE_HOST = '(x86_64|i.86|powerpc|sparc|s390|arm|aarch64).*-linux'
# musl is currently broken entirely
COMPATIBLE_HOST_libc-musl = 'null'

FILES_libasan += "${libdir}/libasan.so.*"
FILES_libasan-dev += "\
    ${libdir}/libasan_preinit.o \
    ${libdir}/libasan.so \
    ${libdir}/libasan.la \
"
FILES_libasan-staticdev += "${libdir}/libasan.a"

FILES_libubsan += "${libdir}/libubsan.so.*"
FILES_libubsan-dev += "\
    ${libdir}/libubsan.so \
    ${libdir}/libubsan.la \
"
FILES_libubsan-staticdev += "${libdir}/libubsan.a"

FILES_liblsan += "${libdir}/liblsan.so.*"
FILES_liblsan-dev += "\
    ${libdir}/liblsan.so \
    ${libdir}/liblsan.la \
    ${libdir}/liblsan_preinit.o \
"
FILES_liblsan-staticdev += "${libdir}/liblsan.a"

FILES_libtsan += "${libdir}/libtsan.so.*"
FILES_libtsan-dev += "\
    ${libdir}/libtsan.so \
    ${libdir}/libtsan.la \
    ${libdir}/libtsan_*.o \
"
FILES_libtsan-staticdev += "${libdir}/libtsan.a"

FILES_${PN} = "${libdir}/*.spec ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/sanitizer/*.h"
