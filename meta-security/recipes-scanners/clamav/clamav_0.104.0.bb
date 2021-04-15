SUMMARY = "ClamAV anti-virus utility for Unix - command-line interface"
DESCRIPTION = "ClamAV is an open source antivirus engine for detecting trojans, viruses, malware & other malicious threats."
HOMEPAGE = "http://www.clamav.net/index.html"
SECTION = "security"
LICENSE = "LGPL-2.1"

DEPENDS = "glibc llvm libtool db openssl zlib curl libxml2 bison pcre2 json-c libcheck"
 
LIC_FILES_CHKSUM = "file://COPYING.txt;beginline=2;endline=3;md5=f7029fbbc5898b273d5902896f7bbe17"

SRCREV = "5553a5e206ceae5d920368baee7d403f823bcb6f"

SRC_URI = "git://github.com/vrtadmin/clamav-devel;branch=dev/0.104 \
    file://clamd.conf \
    file://freshclam.conf \
    file://volatiles.03_clamav \
    file://tmpfiles.clamav \
    file://${BPN}.service \
    file://headers_fixup.patch \
    file://oe_cmake_fixup.patch \
"
S = "${WORKDIR}/git"

LEAD_SONAME = "libclamav.so"
SO_VER = "9.6.0"

BINCONFIG = "${bindir}/clamav-config"

inherit cmake chrpath pkgconfig useradd systemd multilib_header multilib_script

CLAMAV_UID ?= "clamav"
CLAMAV_GID ?= "clamav"

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/clamav-config"

EXTRA_OECMAKE = " -DCMAKE_BUILD_TYPE=Release -DOPTIMIZE=ON -DENABLE_JSON_SHARED=OFF \
                  -DCLAMAV_GROUP=${CLAMAV_GID} -DCLAMAV_USER=${CLAMAV_UID} \ 
                  -DENABLE_TESTS=OFF -DBUILD_SHARED_LIBS=ON \
                  -DDISABLE_MPOOL=ON -DENABLE_FRESHCLAM_DNS_FIX=ON \
                   "

PACKAGECONFIG ?= "  clamonacc \
                 ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd", "", d)}"

PACKAGECONFIG[milter] = "-DENABLE_MILTER=ON ,-DENABLE_MILTER=OFF, curl, curl"
PACKAGECONFIG[clamonacc] = "-DENABLE_CLAMONACC=ON ,-DENABLE_CLAMONACC=OFF,"
PACKAGECONFIG[unrar] = "-DENABLE_UNRAR=ON ,-DENABLE_UNRAR=OFF,"
PACKAGECONFIG[systemd] = "-DENABLE_SYSTEMD=ON -DSYSTEMD_UNIT_DIR=${systemd_system_unitdir}, -DENABLE_SYSTEMD=OFF, systemd"

export OECMAKE_C_FLAGS += " -I${STAGING_INCDIR} -L ${RECIPE_SYSROOT}${nonarch_libdir} -L${STAGING_LIBDIR} -lpthread" 

do_install_append () {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${localstatedir}/lib/clamav
    install -d ${D}${sysconfdir}/clamav ${D}${sysconfdir}/default/volatiles

    install -m 644 ${WORKDIR}/clamd.conf ${D}/${prefix}/${sysconfdir}
    install -m 644 ${WORKDIR}/freshclam.conf ${D}/${prefix}/${sysconfdir}
    install -m 0644 ${WORKDIR}/volatiles.03_clamav  ${D}${sysconfdir}/default/volatiles/03_clamav
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/libclamav.pc
    rm ${D}/${libdir}/libclamav.so
    if [ "${INSTALL_CLAMAV_CVD}" = "1" ]; then
        install -m 666 ${S}/clamav_db/* ${D}/${localstatedir}/lib/clamav/.
    fi

    rm ${D}/${libdir}/libfreshclam.so
    rm ${D}/${libdir}/libmspack.so

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


PACKAGES = "${PN} ${PN}-dev ${PN}-dbg ${PN}-daemon ${PN}-doc \
            ${PN}-clamdscan ${PN}-freshclam ${PN}-libclamav ${PN}-staticdev"

FILES_${PN} = "${bindir}/clambc ${bindir}/clamscan ${bindir}/clamsubmit ${sbindir}/clamonacc \
                ${bindir}/*sigtool ${mandir}/man1/clambc* ${mandir}/man1/clamscan* \
                ${mandir}/man1/sigtool* ${mandir}/man1/clambsubmit*  \
                ${docdir}/clamav/* ${libdir}/libmspack* "

FILES_${PN}-clamdscan = " ${bindir}/clamdscan \
                        ${docdir}/clamdscan/* \
                        ${mandir}/man1/clamdscan* \
                        "

FILES_${PN}-daemon = "${bindir}/clamconf ${bindir}/clamdtop ${sbindir}/clamd \
                        ${mandir}/man1/clamconf* ${mandir}/man1/clamdtop* \
                        ${mandir}/man5/clamd*  ${mandir}/man8/clamd* \
                        ${sysconfdir}/clamd.conf* \
                        /usr/etc/clamd.conf* \
                        ${systemd_unitdir}/system/clamav-daemon/* \
                        ${docdir}/clamav-daemon/*  ${sysconfdir}/clamav-daemon \
                        ${sysconfdir}/logcheck/ignore.d.server/clamav-daemon \
                        ${systemd_unitdir}/system/clamav-daemon.service \
                        ${systemd_unitdir}/system/clamav-clamonacc.service \
                        "

FILES_${PN}-freshclam = "${bindir}/freshclam \
                        ${sysconfdir}/freshclam.conf*  \
                        /usr/etc/freshclam.conf*  \
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

FILES_${PN}-libclamav = "${libdir}/libclamav.so* ${libdir}/libclammspack.so* \
                         ${libdir}/libfreshclam.so* ${docdir}/libclamav/* "

FILES_${PN}-doc = "${mandir}/man/* \
                   ${datadir}/man/* \
                   ${docdir}/* "

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
