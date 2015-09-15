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
    mtarget=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
    target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
    if [ -d ${STAGING_INCDIR_NATIVE}/gcc-build-internal-$mtarget ]; then
        hardlinkdir ${STAGING_INCDIR_NATIVE}/gcc-build-internal-$mtarget ${B}
    fi

    echo "Configuring libsanitizer"
    rm -rf ${B}/$target/libsanitizer/
    mkdir -p ${B}/$target/libsanitizer/
    # This is kind of gross, but it's an easy way to make configure happy
    # without hacking it up to use the system stdc++ instead of the one it
    # expects to be newly built.
    rm -rf ${B}/$target/libstdc++-v3/
    mkdir -p ${B}/$target/libstdc++-v3/src/
    ln -s ${STAGING_LIBDIR}/libstdc++.la ${B}/$target/libstdc++-v3/src/
    ln -s ${STAGING_LIBDIR}/libstdc++.so ${B}/$target/libstdc++-v3/src/
    cd ${B}/$target/libsanitizer/
    chmod a+x ${S}/libsanitizer/configure
    ${S}/libsanitizer/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
    # Easiest way to stop bad RPATHs getting into the library since we have a
    # broken libtool here
    sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${B}/$target/libsanitizer/libtool
}

do_compile () {
    target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
    cd ${B}/$target/libsanitizer/
    oe_runmake MULTIBUILDTOP=${B}/$target/libsanitizer/
}

do_install () {
    target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
    cd ${B}/$target/libsanitizer/
    oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/$target/libsanitizer/ install
    if [ -d ${D}${infodir} ]; then
        rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
    fi
    chown -R root:root ${D}
}

INHIBIT_DEFAULT_DEPS = "1"
ALLOW_EMPTY_${PN} = "1"
DEPENDS = "gcc-runtime"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "${PN}"
PACKAGES += "libasan libubsan liblsan libtsan"
PACKAGES += "libasan-dev libubsan-dev liblsan-dev libtsan-dev"
PACKAGES += "libasan-dbg libubsan-dbg liblsan-dbg libtsan-dbg"
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
RRECOMMENDS_${PN}_append_x86-64 = " liblsan libtsan"
RRECOMMENDS_${PN}_append_x86 = " liblsan"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata"

# MIPS, aarch64, and SPARC are broken.
COMPATIBLE_HOST = '(x86_64|i.86|powerpc|arm).*-linux'

FILES_libasan += "${libdir}/libasan.so.*"
FILES_libasan-dbg += "${libdir}/.debug/libasan.so.*"
FILES_libasan-dev += "\
    ${libdir}/libasan_preinit.o \
    ${libdir}/libasan.so \
    ${libdir}/libasan.la \
"
FILES_libasan-staticdev += "${libdir}/libasan.a"

FILES_libubsan += "${libdir}/libubsan.so.*"
FILES_libubsan-dbg += "${libdir}/.debug/libubsan.so.*"
FILES_libubsan-dev += "\
    ${libdir}/libubsan.so \
    ${libdir}/libubsan.la \
"
FILES_libubsan-staticdev += "${libdir}/libubsan.a"

FILES_liblsan += "${libdir}/liblsan.so.*"
FILES_liblsan-dbg += "${libdir}/.debug/liblsan.so.*"
FILES_liblsan-dev += "\
    ${libdir}/liblsan.so \
    ${libdir}/liblsan.la \
"
FILES_liblsan-staticdev += "${libdir}/liblsan.a"

FILES_libtsan += "${libdir}/libtsan.so.*"
FILES_libtsan-dbg += "${libdir}/.debug/libtsan.so.*"
FILES_libtsan-dev += "\
    ${libdir}/libtsan.so \
    ${libdir}/libtsan.la \
"
FILES_libtsan-staticdev += "${libdir}/libtsan.a"

FILES_${PN} = "${libdir}/*.spec ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/sanitizer/*.h"
