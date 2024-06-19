SUMMARY = "Socket-based service activation daemon"
HOMEPAGE = "https://github.com/xinetd-org/xinetd"
DESCRIPTION = "xinetd is a powerful replacement for inetd, xinetd has access control mechanisms, extensive logging capabilities, the ability to make services available based on time, can place limits on the number of servers that can be started, and has deployable defence mechanisms to protect against port scanners, among other things."

LICENSE = "xinetd"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=55c5fdf02cfcca3fc9621b6f2ceae10f"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

SRC_URI = "git://github.com/openSUSE/xinetd.git;protocol=https;branch=master \
           file://xinetd.init \
           file://xinetd.default \
           file://xinetd.service \
           "

SRCREV = "6a4af7786630ce48747d9687e2f18f45ea6684c4"

S = "${WORKDIR}/git"

# https://github.com/xinetd-org/xinetd/pull/10 is merged into this git tree revision
CVE_STATUS[CVE-2013-4342] = "fixed-version: Fixed directly in git tree revision"

inherit autotools update-rc.d systemd pkgconfig

SYSTEMD_SERVICE:${PN} = "xinetd.service"

INITSCRIPT_NAME = "xinetd"
INITSCRIPT_PARAMS = "defaults"

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--with-libwrap,,tcp-wrappers"

CFLAGS += "-D_GNU_SOURCE"

CONFFILES:${PN} = "${sysconfdir}/xinetd.conf"

do_install:append() {
       install -d "${D}${sysconfdir}/init.d"
       install -d "${D}${sysconfdir}/default"
       install -m 755 "${UNPACKDIR}/xinetd.init" "${D}${sysconfdir}/init.d/xinetd"
       install -m 644 "${UNPACKDIR}/xinetd.default" "${D}${sysconfdir}/default/xinetd"

       # Install systemd unit files
       install -d ${D}${systemd_system_unitdir}
       install -m 0644 ${UNPACKDIR}/xinetd.service ${D}${systemd_system_unitdir}
       sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
              -e 's,@SBINDIR@,${sbindir},g' \
              ${D}${systemd_system_unitdir}/xinetd.service
}

# Script for converting inetd.conf files into xinetd.conf files
PACKAGES =+ "${PN}-xconv"
FILES:${PN}-xconv = "${bindir}/xconv.pl"
RDEPENDS:${PN}-xconv += "perl"
