SUMMARY = "Cherokee Web Server fast and secure"
SUMMARY:cget = "Small downloader based in the Cherokee client library"
HOMEPAGE = "http://www.cherokee-project.com/"
SECTION = "network"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "unzip-native libpcre openssl mysql5 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRCREV = "9a75e65b876bcc376cb6b379dca1f7ce4a055c59"
PV = "1.2.104+git${SRCPV}"
SRC_URI = "git://github.com/cherokee/webserver;branch=master;protocol=https \
           file://cherokee.init \
           file://cherokee.service \
           file://cherokee-install-configured.py-once.patch \
           file://0001-configure.ac-Add-foreign-to-AM_INIT_AUTOMAKE.patch \
           file://0001-make-Do-not-build-po-files.patch \
           file://0001-common-internal.h-Define-LLONG_MAX-if-undefined.patch \
"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig binconfig update-rc.d systemd ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "pythonnative", "", d)}

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ffmpeg] = "--with-ffmpeg,--without-ffmpeg,libav"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[geoip] = "--with-geoip,--without-geoip,geoip"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

EXTRA_OECONF = "--disable-static \
                --disable-nls \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)} \
               --with-wwwroot=${localstatedir}/www/cherokee \
"

do_install:append () {
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

FILES:cget = "${bindir}/cget"
FILES:libcherokee-server = "${libdir}/libcherokee-server${SOLIBS}"
FILES:libcherokee-client = "${libdir}/libcherokee-client${SOLIBS}"
FILES:libcherokee-base = "${libdir}/libcherokee-base${SOLIBS}"

# Pack the htdocs
FILES:${PN} += "${localstatedir}/www/cherokee"

CONFFILES:${PN} = " \
                   ${sysconfdir}/cherokee/cherokee.conf \
                   ${sysconfdir}/init.d/cherokee \
"

INITSCRIPT_NAME = "cherokee"
INITSCRIPT_PARAMS = "defaults 91 91"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "cherokee.service"

python() {
    if 'meta-python2' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-python2 to be present.')
}

CVE_PRODUCT += "cherokee_web_server"
