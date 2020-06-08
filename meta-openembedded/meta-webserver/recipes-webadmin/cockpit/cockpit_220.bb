SUMMARY = "Admin interface for Linux machines"
DESCRIPTION = "Cockpit makes it easy to administer your GNU/Linux servers via a web browser"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI += " \
    https://github.com/cockpit-project/cockpit/releases/download/${PV}/cockpit-${PV}.tar.xz \
    file://0001-remove-tests-dep-on-gobject-intro.patch \
    file://0002-fix-makefile-use-copy-rule-for-unmodified-files.patch \
    file://cockpit.pam \
    "
SRC_URI[md5sum] = "beb88d8e70ee1da6ebd917c956217803"
SRC_URI[sha256sum] = "afc82acc8ef9d51e0f34265a07a2f059f5b71a1df721b299e657a40a098cbb7f"

inherit gettext pkgconfig autotools systemd features_check

DEPENDS += "glib-2.0-native intltool-native gnutls virtual/gettext json-glib krb5 libpam systemd"

COMPATIBLE_HOST_libc-musl = "null"

RDEPENDS_${PN} += "glib-networking"

REQUIRED_DISTRO_FEATURES = "systemd pam"

COCKPIT_USER_GROUP ?= "root"
COCKPIT_WS_USER_GROUP ?= "${COCKPIT_USER_GROUP}"

EXTRA_AUTORECONF = "-I tools"
EXTRA_OECONF = " \
    --with-cockpit-user=${COCKPIT_USER_GROUP} \
    --with-cockpit-group=${COCKPIT_USER_GROUP} \
    --with-cockpit-ws-instance-user=${COCKPIT_WS_USER_GROUP} \
    --with-cockpit-ws-instance-group=${COCKPIT_WS_USER_GROUP} \
    --disable-doc \
    --with-systemdunitdir=${systemd_system_unitdir} \
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)} \
"

PACKAGECONFIG[pcp] = "--enable-pcp,--disable-pcp,pcp"
PACKAGECONFIG[dashboard] = "--enable-ssh,--disable-ssh,libssh"
PACKAGECONFIG[storaged] = ",,,udisks2"
PACKAGECONFIG[polkit] = "--enable-polkit,--disable-polkit,polkit"

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
    ${PN}-machines \
    ${PN}-selinux \
    ${PN}-playground \
    ${PN}-docker \
    ${PN}-dashboard \
    ${PN}-bridge \
    ${PN}-ws \
    ${PN}-desktop \
"
SYSTEMD_PACKAGES = "${PN}-ws"

FILES_${PN}-pcp = " \
    ${libexecdir}/cockpit-pcp \
    ${datadir}/cockpit/pcp \
    ${localstatedir}/lib/pcp/config/pmlogconf/tools/cockpit \
"
FILES_${PN}-realmd = "${datadir}/cockpit/realmd"
FILES_${PN}-tuned = "${datadir}/cockpit/tuned"
FILES_${PN}-shell = "${datadir}/cockpit/shell"
FILES_${PN}-systemd = "${datadir}/cockpit/systemd"
FILES_${PN}-users = "${datadir}/cockpit/users"
FILES_${PN}-kdump = " \
    ${datadir}/cockpit/kdump \
    ${datadir}/metainfo/org.cockpit-project.cockpit-kdump.metainfo.xml \
"
FILES_${PN}-sosreport = " \
    ${datadir}/cockpit/sosreport \
    ${datadir}/metainfo/org.cockpit-project.cockpit-sosreport.metainfo.xml \
    ${datadir}/pixmaps/cockpit-sosreport.png \
"
FILES_${PN}-storaged = " \
    ${datadir}/cockpit/storaged \
    ${datadir}/metainfo/org.cockpit-project.cockpit-storaged.metainfo.xml \
"

FILES_${PN}-networkmanager = "${datadir}/cockpit/networkmanager"
RDEPENDS_${PN}-networkmanager = "networkmanager"

FILES_${PN}-machines = " \
    ${datadir}/cockpit/machines \
    ${datadir}/metainfo/org.cockpit-project.cockpit-machines.metainfo.xml \
"
FILES_${PN}-selinux = " \
    ${datadir}/cockpit/selinux \
    ${datadir}/metainfo/org.cockpit-project.cockpit-selinux.metainfo.xml \
"
FILES_${PN}-playground = "${datadir}/cockpit/playground"
FILES_${PN}-docker = " \
    ${datadir}/cockpit/docker \
    ${datadir}/metainfo/org.cockpit-project.cockpit-docker.metainfo.xml \
"
FILES_${PN}-dashboard = "${datadir}/cockpit/dashboard"
ALLOW_EMPTY_${PN}-dashboard = "1"

FILES_${PN}-bridge = " \
    ${bindir}/cockpit-bridge \
    ${libexec}/cockpit-askpass \
"
RDEPENDS_${PN}-bridge = ""

FILES_${PN}-desktop = "${libexecdir}/cockpit-desktop"
RDEPENDS_${PN}-desktop += "bash"

FILES_${PN}-ws = " \
    ${sysconfdir}/cockpit/ws-certs.d \
    ${sysconfdir}/pam.d/cockpit \
    ${sysconfdir}/issue.d/cockpit.issue \
    ${sysconfdir}/motd.d/cockpit \
    ${datadir}/cockpit/motd/update-motd \
    ${datadir}/cockpit/motd/inactive.motd \
    ${systemd_system_unitdir}/cockpit.service \
    ${systemd_system_unitdir}/cockpit-motd.service \
    ${systemd_system_unitdir}/cockpit.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-http.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-http.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-http-redirect.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-http-redirect.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-https-factory.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-https-factory@.service \
    ${systemd_system_unitdir}/cockpit-wsinstance-https@.socket \
    ${systemd_system_unitdir}/cockpit-wsinstance-https@.service \
    ${systemd_system_unitdir}/system-cockpithttps.slice \
    ${libdir}/tmpfiles.d/cockpit-tempfiles.conf \
    ${sbindir}/remotectl \
    ${libdir}/security/pam_ssh_add.so \
    ${libdir}/security/pam_cockpit_cert.so \
    ${libexecdir}/cockpit-ws \
    ${libexecdir}/cockpit-wsinstance-factory \
    ${libexecdir}/cockpit-tls \
    ${libexecdir}/cockpit-session \
    ${localstatedir}/lib/cockpit \
    ${datadir}/cockpit/static \
    ${datadir}/cockpit/branding \
"
CONFFILES_${PN}-ws += " \
    ${sysconfdir}/issue.d/cockpit.issue \
    ${sysconfdir}/motd.d/cockpit \
"
RDEPENDS_${PN}-ws += "openssl-bin"
SYSTEMD_SERVICE_${PN}-ws = "cockpit.socket"

FILES_${PN} += " \
    ${datadir}/cockpit/base1 \
    ${sysconfdir}/cockpit/machines.d \
    ${datadir}/polkit-1/actions/org.cockpit-project.cockpit-bridge.policy \
    ${datadir}/cockpit/ssh \
    ${libexecdir}/cockpit-ssh \
    ${datadir}/cockpit \
    ${datadir}/metainfo/cockpit.appdata.xml \
    ${datadir}/pixmaps/cockpit.png \
"
RDEPENDS_${PN} += "${PN}-bridge"

do_install_append() {
    pkgdatadir=${datadir}/cockpit

    chmod 4750 ${D}${libexecdir}/cockpit-session

    install -d "${D}${sysconfdir}/pam.d"
    install -p -m 0644 ${WORKDIR}/cockpit.pam ${D}${sysconfdir}/pam.d/cockpit

    # provided by firewalld
    rm -rf ${D}${libdir}/firewalld

    if ! ${@bb.utils.contains('PACKAGECONFIG', 'storaged', 'true', 'false', d)}; then
        for filename in ${FILES_${PN}-storaged}
        do
            rm -rf ${D}$filename
        done
    fi
}
