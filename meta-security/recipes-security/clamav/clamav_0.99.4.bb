SUMMARY = "ClamAV anti-virus utility for Unix - command-line interface"
DESCRIPTION = "ClamAV is an open source antivirus engine for detecting trojans, viruses, malware & other malicious threats."
HOMEPAGE = "http://www.clamav.net/index.html"
SECTION = "security"
LICENSE = "LGPL-2.1"

DEPENDS = "libtool db libmspack openssl zlib llvm chrpath-replacement-native clamav-native"
DEPENDS_class-native = "db-native openssl-native zlib-native"
 
LIC_FILES_CHKSUM = "file://COPYING.LGPL;beginline=2;endline=3;md5=4b89c05acc71195e9a06edfa2fa7d092"

SRCREV = "b66e5e27b48c0a07494f9df9b809ed933cede047"

SRC_URI = "git://github.com/vrtadmin/clamav-devel;branch=rel/0.99 \
    file://clamd.conf \
    file://freshclam.conf \
    file://volatiles.03_clamav \
    file://tmpfiles.clamav \
    file://${BPN}.service \
    file://freshclam-native.conf \
    "

S = "${WORKDIR}/git"

LEAD_SONAME = "libclamav.so"
SO_VER = "7.1.1"

EXTRANATIVEPATH += "chrpath-native"

inherit autotools-brokensep pkgconfig useradd systemd

UID = "clamav"
GID = "clamav"
INSTALL_CLAMAV_CVD ?= "1"

# Clamav has a built llvm version 2 but does not build with gcc 6.x,
# disable the internal one. This is a known issue
# If you want LLVM support, use the one in core

CLAMAV_USR_DIR = "${STAGING_DIR_NATIVE}/usr"
CLAMAV_USR_DIR_class-target = "${STAGING_DIR_HOST}/usr"

PACKAGECONFIG_class-target ?= "ncurses bz2"
PACKAGECONFIG_class-target += " ${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)}"
PACKAGECONFIG_class-target += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[pcre] = "--with-pcre=${STAGING_LIBDIR},  --without-pcre, libpcre"
PACKAGECONFIG[xml] = "--with-xml=${CLAMAV_USR_DIR}, --disable-xml, libxml2,"
PACKAGECONFIG[json] = "--with-libjson=${STAGING_LIBDIR}, --without-libjson, json,"
PACKAGECONFIG[curl] = "--with-libcurl=${STAGING_LIBDIR}, --without-libcurl, curl,"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6"
PACKAGECONFIG[bz2] = "--with-libbz2-prefix=${CLAMAV_USR_DIR}, --without-libbz2-prefix, "
PACKAGECONFIG[ncurses] = "--with-libncurses-prefix=${CLAMAV_USR_DIR}, --without-libncurses-prefix, ncurses, "
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/, --without-systemdsystemunitdir, "

EXTRA_OECONF_CLAMAV = "--without-libcheck-prefix --disable-unrar \
            --with-system-llvm --with-llvm-linking=dynamic --disable-llvm \
            --disable-mempool \
            --program-prefix="" \
            --disable-yara \
            --disable-xml \
            --with-openssl=${CLAMAV_USR_DIR} \
            --with-zlib=${CLAMAV_USR_DIR} --disable-zlib-vcheck \
            "

EXTRA_OECONF_class-native += "${EXTRA_OECONF_CLAMAV}"
EXTRA_OECONF_class-target += "--with-user=${UID}  --with-group=${GID} --disable-rpath ${EXTRA_OECONF_CLAMAV}"

do_configure () {
    ${S}/configure ${CONFIGUREOPTS} ${EXTRA_OECONF} 
    install -d ${S}/clamav_db
}

do_configure_class-native () {
    ${S}/configure ${CONFIGUREOPTS} ${EXTRA_OECONF} 
}


do_compile_append_class-target() {
    # brute force removing RPATH
    chrpath -d  ${B}/libclamav/.libs/libclamav.so.${SO_VER}
    chrpath -d  ${B}/sigtool/.libs/sigtool
    chrpath -d  ${B}/clambc/.libs/clambc
    chrpath -d  ${B}/clamscan/.libs/clamscan
    chrpath -d  ${B}/clamconf/.libs/clamconf
    chrpath -d  ${B}/clamd/.libs/clamd
    chrpath -d  ${B}/freshclam/.libs/freshclam

    if [ "${INSTALL_CLAMAV_CVD}" = "1" ]; then
        bbnote "CLAMAV creating cvd"
        ${STAGING_BINDIR_NATIVE}/freshclam --datadir=${S}/clamav_db --config=${WORKDIR}/freshclam-native.conf
    fi
}

do_install_append_class-target () {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${localstatedir}/lib/clamav
    install -d ${D}${sysconfdir}/clamav ${D}${sysconfdir}/default/volatiles

    install -m 644 ${WORKDIR}/clamd.conf ${D}/${sysconfdir}
    install -m 644 ${WORKDIR}/freshclam.conf ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/volatiles.03_clamav  ${D}${sysconfdir}/default/volatiles/volatiles.03_clamav
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/libclamav.pc
    rm ${D}/${libdir}/libclamav.so
    install -m 666 ${S}/clamav_db/* ${D}/${localstatedir}/lib/clamav/.
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -D -m 0644 ${WORKDIR}/clamav.service ${D}${systemd_unitdir}/system/clamav.service
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 0644 ${WORKDIR}/tmpfiles.clamav ${D}${sysconfdir}/tmpfiles.d/clamav.conf
    fi
}

pkg_postinst_ontarget_${PN} () {
    if command -v systemd-tmpfiles >/dev/null; then
        systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/clamav.conf
    elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
        ${sysconfdir}/init.d/populate-volatile.sh update
    fi
    mkdir -p ${localstatedir}/lib/clamav
    chown -R ${UID}:${GID} ${localstatedir}/lib/clamav
}


PACKAGES = "${PN} ${PN}-dev ${PN}-dbg ${PN}-daemon ${PN}-doc ${PN}-cvd \
            ${PN}-clamdscan ${PN}-freshclam ${PN}-libclamav ${PN}-staticdev"

FILES_${PN} = "${bindir}/clambc ${bindir}/clamscan ${bindir}/clamsubmit \
                ${bindir}/*sigtool ${mandir}/man1/clambc* ${mandir}/man1/clamscan* \
                ${mandir}/man1/sigtool* ${mandir}/man1/clambsubmit*  \
                ${docdir}/clamav/* "

FILES_${PN}-clamdscan = " ${bindir}/clamdscan \
                        ${docdir}/clamdscan/* \
                        ${mandir}/man1/clamdscan* \
                        "

FILES_${PN}-daemon = "${bindir}/clamconf ${bindir}/clamdtop ${sbindir}/clamd \
                        ${mandir}/man1/clamconf* ${mandir}/man1/clamdtop* \
                        ${mandir}/man5/clamd*  ${mandir}/man8/clamd* \
                        ${sysconfdir}/clamd.conf* \
                        ${systemd_unitdir}/system/clamav-daemon/* \
                        ${docdir}/clamav-daemon/*  ${sysconfdir}/clamav-daemon \
                        ${sysconfdir}/logcheck/ignore.d.server/clamav-daemon "

FILES_${PN}-freshclam = "${bindir}/freshclam \
                        ${sysconfdir}/freshclam.conf*  \
                        ${sysconfdir}/clamav ${sysconfdir}/default/volatiles \
                        ${sysconfdir}/tmpfiles.d/*.conf \
                        ${localstatedir}/lib/clamav \
                        ${docdir}/${PN}-freshclam ${mandir}/man1/freshclam.* \
                        ${mandir}/man5/freshclam.conf.* \
                        ${systemd_unitdir}/system/clamav-freshclam.service"

FILES_${PN}-dev = " ${bindir}/clamav-config ${libdir}/*.la \
                    ${libdir}/pkgconfig/*.pc \
                    ${mandir}/man1/clamav-config.* \
                    ${includedir}/*.h ${docdir}/libclamav* "

FILES_${PN}-staticdev = "${libdir}/*.a"

FILES_${PN}-libclamav = "${libdir}/libclamav.so* ${libdir}/libmspack.so*\
                          ${docdir}/libclamav/* "

FILES_${PN}-doc = "${mandir}/man/* \
                   ${datadir}/man/* \
                   ${docdir}/* "

FILES_${PN}-cvd =  "${localstatedir}/lib/clamav/*.cvd ${localstatedir}/lib/clamav/*.dat"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${UID}"
USERADD_PARAM_${PN} = "--system -g ${GID} --home-dir  \
    ${localstatedir}/spool/${BPN} \
    --no-create-home  --shell /bin/false ${BPN}"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"

RDEPENDS_${PN} = "openssl ncurses-libncurses libbz2 ncurses-libtinfo clamav-freshclam clamav-libclamav"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
