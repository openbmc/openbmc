SUMMARY = "Cherokee Web Server fast and secure"
SUMMARY_cget = "Small downloader based in the Cherokee client library"
HOMEPAGE = "http://www.cherokee-project.com/"
SECTION = "network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

PR = "r9"

DEPENDS = "libpcre openssl mysql5 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRC_URI = "http://www.cherokee-project.de/mirrors/cherokee/1.2/${PV}/cherokee-${PV}.tar.gz \
           file://cherokee.init \
           file://cherokee.service \
           file://cherokee-install-configured.py-once.patch \
"
SRC_URI[md5sum] = "21b01e7d45c0e82ecc0c4257a9c27feb"
SRC_URI[sha256sum] = "042b5687b1a3db3ca818167548ce5d32c35e227c6640732dcb622a6f4a078b7d"

inherit autotools pkgconfig binconfig update-rc.d systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[ffmpeg] = "--with-ffmpeg,--without-ffmpeg,libav"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[geoip] = "--with-geoip,--without-geoip,geoip"

EXTRA_OECONF = "--disable-static \
                --disable-nls \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)} \
               --with-wwwroot=${localstatedir}/www/cherokee \
"

do_install_append () {
    install -m 0755 -d ${D}${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/cherokee.init ${D}${sysconfdir}/init.d/cherokee

    # clean up .la files for plugins
    rm -f ${D}${libdir}/cherokee/*.la

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/cherokee.service ${D}${systemd_unitdir}/system
    rmdir "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"
}

# Put -dev near the front so we can move the .la files into it with a wildcard
PACKAGES =+ "libcherokee-server libcherokee-client libcherokee-base cget"

FILES_cget = "${bindir}/cget"
FILES_libcherokee-server = "${libdir}/libcherokee-server${SOLIBS}"
FILES_libcherokee-client = "${libdir}/libcherokee-client${SOLIBS}"
FILES_libcherokee-base = "${libdir}/libcherokee-base${SOLIBS}"

# Pack the htdocs
FILES_${PN} += "${localstatedir}/www/cherokee"

CONFFILES_${PN} = " \
                   ${sysconfdir}/cherokee/cherokee.conf \
                   ${sysconfdir}/init.d/cherokee \
"

INITSCRIPT_NAME = "cherokee"
INITSCRIPT_PARAMS = "defaults 91 91"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "cherokee.service"
