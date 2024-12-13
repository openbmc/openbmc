SUMMARY = "ClamAV anti-virus utility for Unix - command-line interface"
DESCRIPTION = "ClamAV is an open source antivirus engine for detecting trojans, viruses, malware & other malicious threats."
HOMEPAGE = "http://www.clamav.net/index.html"
SECTION = "security"
LICENSE = "LGPL-2.1-only"

DEPENDS = "glibc llvm libtool db openssl zlib curl libxml2 bison pcre2 json-c libcheck"
 
COMPATIBLE_HOST:libc-musl:class-target = "null"

LIC_FILES_CHKSUM = "file://COPYING.txt;beginline=2;endline=3;md5=f7029fbbc5898b273d5902896f7bbe17"

# July 30th, 2022
SRCREV = "563ba93052f3b7b46fb8725a65ee6299a9c332cf"

SRC_URI = "git://github.com/Cisco-Talos/clamav;branch=rel/0.104;protocol=https \
    file://clamd.conf \
    file://freshclam.conf \
    file://volatiles.03_clamav \
    file://tmpfiles.clamav \
    file://headers_fixup.patch \
    file://oe_cmake_fixup.patch \
"
S = "${UNPACKDIR}/git"

LEAD_SONAME = "libclamav.so"
SO_VER = "9.6.0"

BINCONFIG = "${bindir}/clamav-config"

inherit cmake chrpath pkgconfig useradd systemd multilib_header multilib_script

UPSTREAM_CHECK_COMMITS = "1"

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

do_install:append () {
    install -d ${D}/${sysconfdir}
    install -d -o ${PN} -g ${CLAMAV_GID} ${D}/${localstatedir}/lib/clamav
    install -d ${D}${sysconfdir}/clamav ${D}${sysconfdir}/default/volatiles

    install -m 644 ${UNPACKDIR}/clamd.conf ${D}/${prefix}/${sysconfdir}
    install -m 644 ${UNPACKDIR}/freshclam.conf ${D}/${prefix}/${sysconfdir}
    install -m 0644 ${UNPACKDIR}/volatiles.03_clamav  ${D}${sysconfdir}/default/volatiles/03_clamav
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/libclamav.pc
    rm ${D}/${libdir}/libclamav.so
    if [ "${INSTALL_CLAMAV_CVD}" = "1" ]; then
        install -m 666 ${S}/clamav_db/* ${D}/${localstatedir}/lib/clamav/.
    fi

    rm ${D}/${libdir}/libfreshclam.so

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 0644 ${UNPACKDIR}/tmpfiles.clamav ${D}${sysconfdir}/tmpfiles.d/clamav.conf
    fi
    oe_multilib_header clamav-types.h
}

pkg_postinst:${PN} () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/clamav.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

PACKAGES += "${PN}-daemon ${PN}-clamdscan ${PN}-freshclam ${PN}-libclamav"

FILES:${PN} = "${bindir}/clambc ${bindir}/clamscan ${bindir}/clamsubmit ${sbindir}/clamonacc \
                ${bindir}/*sigtool ${mandir}/man1/clambc* ${mandir}/man1/clamscan* \
                ${mandir}/man1/sigtool* ${mandir}/man1/clambsubmit*  \
                ${docdir}/clamav/*"

FILES:${PN}-clamdscan = " ${bindir}/clamdscan \
                        ${docdir}/clamdscan/* \
                        ${mandir}/man1/clamdscan* \
                        "

FILES:${PN}-daemon = "${bindir}/clamconf ${bindir}/clamdtop ${sbindir}/clamd \
                        ${mandir}/man1/clamconf* ${mandir}/man1/clamdtop* \
                        ${mandir}/man5/clamd*  ${mandir}/man8/clamd* \
                        ${sysconfdir}/clamd.conf* \
                        /usr/etc/clamd.conf* \
                        ${systemd_system_unitdir}/clamav-daemon/* \
                        ${docdir}/clamav-daemon/*  ${sysconfdir}/clamav-daemon \
                        ${sysconfdir}/logcheck/ignore.d.server/clamav-daemon \
                        ${systemd_system_unitdir}/clamav-daemon.service \
                        ${systemd_system_unitdir}/clamav-clamonacc.service \
                        "

FILES:${PN}-freshclam = "${bindir}/freshclam \
                        ${sysconfdir}/freshclam.conf*  \
                        /usr/etc/freshclam.conf*  \
                        ${sysconfdir}/clamav ${sysconfdir}/default/volatiles \
                        ${sysconfdir}/tmpfiles.d/*.conf \
                        ${localstatedir}/lib/clamav \
                        ${docdir}/${PN}-freshclam ${mandir}/man1/freshclam.* \
                        ${mandir}/man5/freshclam.conf.* \
                        ${systemd_system_unitdir}/clamav-freshclam.service"

FILES:${PN}-dev = " ${bindir}/clamav-config ${libdir}/*.la \
                    ${libdir}/pkgconfig/*.pc \
                    ${mandir}/man1/clamav-config.* \
                    ${includedir}/*.h ${docdir}/libclamav* \
                    ${libdir}/libmspack.so"

FILES:${PN}-staticdev = "${libdir}/*.a"

FILES:${PN}-libclamav = "${libdir}/libclamav.so* ${libdir}/libclammspack.so* \
                         ${libdir}/libfreshclam.so* ${docdir}/libclamav/* \
                         ${libdir}/libmspack* "

FILES:${PN}-doc = "${mandir}/man/* \
                   ${datadir}/man/* \
                   ${docdir}/* "

USERADD_PACKAGES = "${PN}-freshclam "
GROUPADD_PARAM:${PN}-freshclam = "--system ${CLAMAV_UID}"
USERADD_PARAM:${PN}-freshclam = "--system -g ${CLAMAV_GID} --home-dir  \
    ${localstatedir}/lib/${BPN} \
    --no-create-home  --shell /sbin/nologin ${PN}"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_PACKAGES  = "${PN}-daemon ${PN}-freshclam"
SYSTEMD_SERVICE:${PN}-daemon = "clamav-daemon.service"
SYSTEMD_SERVICE:${PN}-freshclam = "clamav-freshclam.service"

INSANE_SKIP:${PN}-libclamav  += "dev-so"

RDEPENDS:${PN} = "openssl ncurses-libncurses libxml2 libbz2 ncurses-libtinfo curl libpcre2 clamav-libclamav"
RRECOMMENDS:${PN} = "clamav-freshclam"
RDEPENDS:${PN}-freshclam = "clamav"
RDEPENDS:${PN}-daemon = "clamav clamav-freshclam"
