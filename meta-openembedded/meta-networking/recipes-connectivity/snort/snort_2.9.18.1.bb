DESCRIPTION = "snort - a free lightweight network intrusion detection system for UNIX and Windows."
HOMEPAGE = "http://www.snort.org/"
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=78fa8ef966b48fbf9095e13cc92377c5"

DEPENDS = "xz libpcap libpcre daq libdnet util-linux daq-native libtirpc bison-native"

SRC_URI = "https://www.snort.org/downloads/archive/snort/${BP}.tar.gz \
    file://snort.init \
    file://volatiles.99_snort \
    file://0001-libpcap-search-sysroot-for-headers.patch \
    file://fix-host-contamination-when-enable-static-daq.patch \
    file://disable-run-test-program-while-cross-compiling.patch \
    file://configure.in-disable-tirpc-checking-for-fedora.patch \
"
SRC_URI[sha256sum] = "da8af0f1b2e4f247d970c6a3c0e83fb6dcd5c84faa21aea49f306f269e8e28aa"

UPSTREAM_CHECK_URI = "https://www.snort.org/downloads"
UPSTREAM_CHECK_REGEX = "snort-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools gettext update-rc.d pkgconfig

INITSCRIPT_NAME = "snort"
INITSCRIPT_PARAMS = "defaults"

EXTRA_OECONF = " \
    --enable-gre \
    --enable-linux-smp-stats \
    --enable-reload \
    --enable-reload-error-restart \
    --enable-targetbased \
    --enable-static-daq \
    --with-dnet-includes=${STAGING_INCDIR} \
    --with-dnet-libraries=${STAGING_LIBDIR} \
    --with-libpcre-includes=${STAGING_INCDIR} \
    --with-libpcre-libraries=${STAGING_LIBDIR} \
    --with-daq-includes=${STAGING_INCDIR} \
    --with-daq-libraries=${STAGING_LIBDIR} \
"

# if you want to disable it, you need to patch configure.in first
# AC_CHECK_HEADERS([openssl/sha.h],, SHA_H="no")
# is called even with --without-openssl-includes
PACKAGECONFIG ?= "openssl lzma"
PACKAGECONFIG[openssl] = "--with-openssl-includes=${STAGING_INCDIR} --with-openssl-libraries=${STAGING_LIBDIR}, --without-openssl-includes --without-openssl-libraries, openssl,"
PACKAGECONFIG[lzma] = "--with-lzma-includes=${STAGING_INCDIR} --with-lzma-libraries=${STAGING_LIBDIR}, --without-lzma-includes --without-lzma-libraries, xz,"
PACKAGECONFIG[appid] = "--enable-open-appid, --disable-open-appid, luajit, bash"

CFLAGS += "-I${STAGING_INCDIR}/tirpc"
LDFLAGS += " -ltirpc"

do_install:append() {
    install -d ${D}${sysconfdir}/snort/rules
    install -d ${D}${sysconfdir}/snort/preproc_rules
    install -d ${D}${sysconfdir}/init.d
    for i in map config conf dtd; do
        cp ${S}/etc/*.$i ${D}${sysconfdir}/snort/
    done

    # fix the hardcoded path and lib name
    # comment out the rules that are not provided
    sed -i -e 's#/usr/local/lib#${libdir}#' \
           -e 's#\.\./\(.*rules\)#${sysconfdir}/snort/\1#' \
           -e 's#\(libsf_engine.so\)#\1.0#' \
           -e 's/^\(include $RULE_PATH\)/#\1/' \
           -e 's/^\(dynamicdetection\)/#\1/' \
           -e '/preprocessor reputation/,/blacklist/ s/^/#/' \
           ${D}${sysconfdir}/snort/snort.conf

    cp ${S}/preproc_rules/*.rules ${D}${sysconfdir}/snort/preproc_rules/
    install -m 755 ${WORKDIR}/snort.init ${D}${sysconfdir}/init.d/snort

    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/volatiles.99_snort ${D}${sysconfdir}/default/volatiles/99_snort

    sed -i -e 's|-ffile-prefix-map[^ ]*||g; s|-fdebug-prefix-map[^ ]*||g; s|-fmacro-prefix-map[^ ]*||g; s|${STAGING_DIR_TARGET}||g' ${D}${libdir}/pkgconfig/*.pc
}

pkg_postinst:${PN}() {
    if [ -z "$D" ] && [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
        ${sysconfdir}/init.d/populate-volatile.sh update
    fi
}

FILES:${PN} += " \
    ${libdir}/snort_dynamicengine/*.so.* \
    ${libdir}/snort_dynamicpreprocessor/*.so.* \
    ${libdir}/snort_dynamicrules/*.so.* \
"
FILES:${PN}-dbg += " \
    ${libdir}/snort_dynamicengine/.debug \
    ${libdir}/snort_dynamicpreprocessor/.debug \
    ${libdir}/snort_dynamicrules/.debug \
"
FILES:${PN}-staticdev += " \
    ${libdir}/snort_dynamicengine/*.a \
    ${libdir}/snort_dynamicpreprocessor/*.a \
    ${libdir}/snort_dynamicrules/*.a \
    ${libdir}/snort/dynamic_preproc/*.a \
    ${libdir}/snort/dynamic_output/*.a \
"
FILES:${PN}-dev += " \
    ${libdir}/snort_dynamicengine/*.la \
    ${libdir}/snort_dynamicpreprocessor/*.la \
    ${libdir}/snort_dynamicrules/*.la \
    ${libdir}/snort_dynamicengine/*.so \
    ${libdir}/snort_dynamicpreprocessor/*.so \
    ${libdir}/snort_dynamicrules/*.so \
    ${prefix}/src/snort_dynamicsrc \
"
