SUMMARY = "ClamAV anti-virus utility for Unix - command-line interface"
DESCRIPTION = "ClamAV is an open source antivirus engine for detecting trojans, viruses, malware & other malicious threats."
HOMEPAGE = "http://www.clamav.net/index.html"
SECTION = "security"
LICENSE = "LGPL-2.1"

DEPENDS = "libtool db libxml2 openssl zlib curl llvm clamav-native libmspack bison-native"
DEPENDS_class-native = "db-native openssl-native zlib-native llvm-native curl-native bison-native"
 
LIC_FILES_CHKSUM = "file://COPYING.LGPL;beginline=2;endline=3;md5=4b89c05acc71195e9a06edfa2fa7d092"

SRCREV = "482fcd413b07e9fd3ef9850e6d01a45f4e187108"

SRC_URI = "git://github.com/vrtadmin/clamav-devel;branch=rel/0.101 \
    file://clamd.conf \
    file://freshclam.conf \
    file://volatiles.03_clamav \
    file://tmpfiles.clamav \
    file://${BPN}.service \
    file://freshclam-native.conf \
    "

S = "${WORKDIR}/git"

LEAD_SONAME = "libclamav.so"
SO_VER = "9.0.4"

inherit autotools pkgconfig useradd systemd multilib_header multilib_script

CLAMAV_UID ?= "clamav"
CLAMAV_GID ?= "clamav"
INSTALL_CLAMAV_CVD ?= "1"

CLAMAV_USR_DIR = "${STAGING_DIR_NATIVE}/usr"
CLAMAV_USR_DIR_class-target = "${STAGING_DIR_HOST}/usr"

PACKAGECONFIG_class-target ?= "ncurses bz2"
PACKAGECONFIG_class-target += " ${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)}"
PACKAGECONFIG_class-target += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[pcre] = "--with-pcre=${STAGING_LIBDIR},  --without-pcre, libpcre"
PACKAGECONFIG[json] = "--with-libjson=${STAGING_LIBDIR}, --without-libjson, json-c,"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6"
PACKAGECONFIG[bz2] = "--with-libbz2-prefix=${CLAMAV_USR_DIR}, --disable-bzip2, bzip2"
PACKAGECONFIG[ncurses] = "--with-libncurses-prefix=${CLAMAV_USR_DIR}, --without-libncurses-prefix, ncurses, "
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/, --without-systemdsystemunitdir, "

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/clamav-config ${PN}-cvd:${localstatedir}/lib/clamav/mirrors.dat"

EXTRA_OECONF_CLAMAV = "--without-libcheck-prefix --disable-unrar \
            --disable-mempool \
            --program-prefix="" \
            --disable-zlib-vcheck \
            --with-xml=${CLAMAV_USR_DIR} \
            --with-zlib=${CLAMAV_USR_DIR} \
            --with-openssl=${CLAMAV_USR_DIR} \
            --with-libcurl=${CLAMAV_USR_DIR} \
            --with-system-libmspack=${CLAMAV_USR_DIR} \
            --with-iconv=no \
            --enable-check=no \
            "

EXTRA_OECONF_class-native += "${EXTRA_OECONF_CLAMAV}"
EXTRA_OECONF_class-target += "--with-user=${CLAMAV_UID}  --with-group=${CLAMAV_GID} ${EXTRA_OECONF_CLAMAV}"

do_configure () {
    ${S}/configure ${CONFIGUREOPTS} ${EXTRA_OECONF} 
}

do_configure_class-native () {
    ${S}/configure ${CONFIGUREOPTS} ${EXTRA_OECONF} 
}

do_compile_append_class-target() {
    if [ "${INSTALL_CLAMAV_CVD}" = "1" ]; then
        bbnote "CLAMAV creating cvd"
        install -d ${S}/clamav_db
        ${STAGING_BINDIR_NATIVE}/freshclam --datadir=${S}/clamav_db --config=${WORKDIR}/freshclam-native.conf
    fi
}

do_install_append_class-target () {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${localstatedir}/lib/clamav
    install -d ${D}${sysconfdir}/clamav ${D}${sysconfdir}/default/volatiles

    install -m 644 ${WORKDIR}/clamd.conf ${D}/${sysconfdir}
    install -m 644 ${WORKDIR}/freshclam.conf ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/volatiles.03_clamav  ${D}${sysconfdir}/default/volatiles/03_clamav
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/libclamav.pc
    rm ${D}/${libdir}/libclamav.so
    if [ "${INSTALL_CLAMAV_CVD}" = "1" ]; then
        install -m 666 ${S}/clamav_db/* ${D}/${localstatedir}/lib/clamav/.
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -D -m 0644 ${WORKDIR}/clamav.service ${D}${systemd_unitdir}/system/clamav.service
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 0644 ${WORKDIR}/tmpfiles.clamav ${D}${sysconfdir}/tmpfiles.d/clamav.conf
    fi
    oe_multilib_header clamav-types.h
}

pkg_postinst_ontarget_${PN} () {
    if command -v systemd-tmpfiles >/dev/null; then
        systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/clamav.conf
    elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
        ${sysconfdir}/init.d/populate-volatile.sh update
    fi
    mkdir -p ${localstatedir}/lib/clamav
    chown -R ${CLAMAV_UID}:${CLAMAV_GID} ${localstatedir}/lib/clamav
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

FILES_${PN}-libclamav = "${libdir}/libclamav.so* ${libdir}/libclammspack.so*\
                          ${docdir}/libclamav/* "

FILES_${PN}-doc = "${mandir}/man/* \
                   ${datadir}/man/* \
                   ${docdir}/* "

FILES_${PN}-cvd =  "${localstatedir}/lib/clamav/*.cvd ${localstatedir}/lib/clamav/*.dat"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${CLAMAV_UID}"
USERADD_PARAM_${PN} = "--system -g ${CLAMAV_GID} --home-dir  \
    ${localstatedir}/spool/${BPN} \
    --no-create-home  --shell /bin/false ${BPN}"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"

RDEPENDS_${PN} = "openssl ncurses-libncurses libxml2 libbz2 ncurses-libtinfo curl libpcre2 clamav-freshclam clamav-libclamav"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
