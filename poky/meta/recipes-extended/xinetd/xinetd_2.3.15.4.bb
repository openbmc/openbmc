SUMMARY = "Socket-based service activation daemon"
HOMEPAGE = "https://github.com/xinetd-org/xinetd"

# xinetd is a BSD-like license
# Apple and Gentoo say BSD here.
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=55c5fdf02cfcca3fc9621b6f2ceae10f"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

SRC_URI = "git://github.com/openSUSE/xinetd.git;protocol=https \
           file://xinetd.init \
           file://xinetd.default \
           file://xinetd.service \
           "

SRCREV = "6a4af7786630ce48747d9687e2f18f45ea6684c4"

S = "${WORKDIR}/git"

inherit autotools update-rc.d systemd pkgconfig

SYSTEMD_SERVICE_${PN} = "xinetd.service"

INITSCRIPT_NAME = "xinetd"
INITSCRIPT_PARAMS = "defaults"

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--with-libwrap,,tcp-wrappers"

CONFFILES_${PN} = "${sysconfdir}/xinetd.conf"

do_install_append() {
       install -d "${D}${sysconfdir}/init.d"
       install -d "${D}${sysconfdir}/default"
       install -m 755 "${WORKDIR}/xinetd.init" "${D}${sysconfdir}/init.d/xinetd"
       install -m 644 "${WORKDIR}/xinetd.default" "${D}${sysconfdir}/default/xinetd"

       # Install systemd unit files
       install -d ${D}${systemd_unitdir}/system
       install -m 0644 ${WORKDIR}/xinetd.service ${D}${systemd_unitdir}/system
       sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
              -e 's,@SBINDIR@,${sbindir},g' \
              ${D}${systemd_unitdir}/system/xinetd.service
}

RDEPENDS_${PN} += "perl"
