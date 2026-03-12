SUMMARY = "Admin interface for Linux machines"
DESCRIPTION = "Cockpit makes it easy to administer your GNU/Linux servers via a web browser"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI += " \
    https://github.com/cockpit-project/cockpit/releases/download/${PV}/cockpit-${PV}.tar.xz \
    file://0001-Warn-not-error-if-xsltproc-is-not-found.patch \
    file://cockpit.pam \
    "
SRC_URI[sha256sum] = "72098a51f85c4a63f2e276af119920a5faca978b57652df562b409b5941b5146"

inherit gettext pkgconfig autotools systemd features_check python3targetconfig

DEPENDS += "glib-2.0-native intltool-native gnutls virtual/gettext json-glib krb5 libpam systemd python3-pip-native python3-setuptools-native"

COMPATIBLE_HOST:libc-musl = "null"

RDEPENDS:${PN} += "glib-networking"

REQUIRED_DISTRO_FEATURES = "systemd pam"

COCKPIT_USER_GROUP ?= "root"

EXTRA_AUTORECONF = "-I tools"
EXTRA_OECONF = " \
    --with-admin-group=${COCKPIT_USER_GROUP} \
    --disable-doc \
    --with-systemdunitdir=${systemd_system_unitdir} \
    --with-pamdir=${base_libdir}/security \
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)} \
"

PACKAGECONFIG[pcp] = ",,pcp"
PACKAGECONFIG[dashboard] = ",,libssh"
PACKAGECONFIG[storaged] = ",,udisks2"
PACKAGECONFIG[polkit] = ",,polkit"

PACKAGES =+ " \
    ${PN}-pcp \
    ${PN}-realmd \
    ${PN}-tuned \
    ${PN}-shell \
    ${PN}-systemd \
    ${PN}-users \
    ${PN}-kdump \
    ${PN}-sosreport \
    ${PN}-storaged \
    ${PN}-networkmanager \
    ${PN}-selinux \
    ${PN}-playground \
    ${PN}-dashboard \
    ${PN}-packagekit \
    ${PN}-apps \
    ${PN}-bridge \
    ${PN}-ws \
    ${PN}-desktop \
"
SYSTEMD_PACKAGES = "${PN}-ws"

FILES:${PN}-pcp = " \
    ${libexecdir}/cockpit-pcp \
    ${datadir}/cockpit/pcp \
    ${localstatedir}/lib/pcp/config/pmlogconf/tools/cockpit \
"
FILES:${PN}-realmd = "${datadir}/cockpit/realmd"
FILES:${PN}-tuned = "${datadir}/cockpit/tuned"
FILES:${PN}-shell = "${datadir}/cockpit/shell"
FILES:${PN}-systemd = "${datadir}/cockpit/systemd"
FILES:${PN}-users = "${datadir}/cockpit/users"
FILES:${PN}-kdump = " \
    ${datadir}/cockpit/kdump \
    ${datadir}/metainfo/org.cockpit_project.cockpit_kdump.metainfo.xml \
"
FILES:${PN}-sosreport = " \
    ${datadir}/cockpit/sosreport \
    ${datadir}/metainfo/org.cockpit_project.cockpit_sosreport.metainfo.xml \
    ${datadir}/pixmaps/cockpit-sosreport.png \
    ${datadir}/icons/hicolor/64x64/apps/cockpit-sosreport.png \
"
FILES:${PN}-storaged = " \
    ${datadir}/cockpit/storaged \
    ${datadir}/metainfo/org.cockpit_project.cockpit_storaged.metainfo.xml \
"

FILES:${PN}-networkmanager = " \
    ${datadir}/cockpit/networkmanager \
    ${datadir}/metainfo/org.cockpit_project.cockpit_networkmanager.metainfo.xml \
"
RDEPENDS:${PN}-networkmanager = "networkmanager"

FILES:${PN}-selinux = " \
    ${datadir}/cockpit/selinux \
    ${datadir}/metainfo/org.cockpit_project.cockpit_selinux.metainfo.xml \
"
FILES:${PN}-playground = "${datadir}/cockpit/playground"
FILES:${PN}-dashboard = "${datadir}/cockpit/dashboard"
ALLOW_EMPTY:${PN}-dashboard = "1"

FILES:${PN}-packagekit = "${datadir}/cockpit/packagekit"
FILES:${PN}-apps = "${datadir}/cockpit/apps"

FILES:${PN}-bridge = " \
    ${bindir}/cockpit-bridge \
    ${libexecdir}/cockpit-askpass \
    ${PYTHON_SITEPACKAGES_DIR} \
"
RDEPENDS:${PN}-bridge = "python3"

FILES:${PN}-desktop = "${libexecdir}/cockpit-desktop"
RDEPENDS:${PN}-desktop += "bash"

FILES:${PN}-ws = " \
    ${sysconfdir}/cockpit/ws-certs.d \
    ${sysconfdir}/pam.d/cockpit \
    ${sysconfdir}/issue.d/cockpit.issue \
    ${sysconfdir}/motd.d/cockpit \
    ${datadir}/cockpit/motd/update-motd \
    ${datadir}/cockpit/motd/inactive.motd \
    ${systemd_system_unitdir}/cockpit.service \
    ${systemd_system_unitdir}/cockpit-motd.service \
    ${systemd_system_unitdir}/cockpit.socket \
    ${systemd_system_unitdir}/cockpit-session.socket \
    ${systemd_system_unitdir}/cockpit-session@.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-http.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-http.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-http-redirect.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-http-redirect.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-https-factory.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-https-factory@.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-https@.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-https@.service \
    ${systemd_system_unitdir}/system-cockpithttps.slice \
    ${systemd_system_unitdir}/cockpit-session-socket-user.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-socket-user.service \
    ${systemd_system_unitdir}/cockpit-issue.service \
    ${libdir}/tmpfiles.d/cockpit-tempfiles.conf \
    ${sbindir}/remotectl \
    ${base_libdir}/security/pam_ssh_add.so \
    ${base_libdir}/security/pam_cockpit_cert.so \
    ${libexecdir}/cockpit-ws \
    ${libexecdir}/cockpit-wsinstance-factory \
    ${libexecdir}/cockpit-tls \
    ${libexecdir}/cockpit-session \
    ${localstatedir}/lib/cockpit \
    ${datadir}/cockpit/static \
    ${datadir}/cockpit/branding \
"
CONFFILES:${PN}-ws += " \
    ${sysconfdir}/issue.d/cockpit.issue \
    ${sysconfdir}/motd.d/cockpit \
"
RDEPENDS:${PN}-ws += "openssl-bin"
SYSTEMD_SERVICE:${PN}-ws = "cockpit.socket"

FILES:${PN} += " \
    ${datadir}/cockpit/base1 \
    ${sysconfdir}/cockpit/machines.d \
    ${datadir}/polkit-1/actions/org.cockpit-project.cockpit-bridge.policy \
    ${datadir}/cockpit/ssh \
    ${libexecdir}/cockpit-ssh \
    ${datadir}/cockpit \
    ${datadir}/icons/hicolor/128x128/apps/cockpit.png \
    ${datadir}/metainfo/org.cockpit_project.cockpit.appdata.xml \
    ${datadir}/pixmaps/cockpit.png \
    ${nonarch_libdir}/tmpfiles.d \
    ${nonarch_libdir}/firewalld \
"
RDEPENDS:${PN} += "${PN}-bridge"
# Needs bash and mv for /usr/libexec/cockpit-certificate-helper
RDEPENDS:${PN} += "bash coreutils"

do_install:append() {
    pkgdatadir=${datadir}/cockpit

    chmod 4750 ${D}${libexecdir}/cockpit-session

    install -d "${D}${sysconfdir}/pam.d"
    install -p -m 0644 ${UNPACKDIR}/cockpit.pam ${D}${sysconfdir}/pam.d/cockpit

    # provided by firewalld
    rm -rf ${D}${libdir}/firewalld \
    ${D}${PYTHON_SITEPACKAGES_DIR}/*/__pycache__ \
    ${D}${PYTHON_SITEPACKAGES_DIR}/*/*/__pycache__ \
    ${D}${PYTHON_SITEPACKAGES_DIR}/*/*/*/__pycache__ \
    ${D}${PYTHON_SITEPACKAGES_DIR}/*/*/*/*/__pycache__ \
    ${D}${PYTHON_SITEPACKAGES_DIR}/${BP}.dist-info/direct_url.json

    if ! ${@bb.utils.contains('PACKAGECONFIG', 'storaged', 'true', 'false', d)}; then
        for filename in ${FILES:${PN}-storaged}
        do
            rm -rf ${D}$filename
        done
    fi
}

CVE_PRODUCT = "cockpit-project:cockpit"
