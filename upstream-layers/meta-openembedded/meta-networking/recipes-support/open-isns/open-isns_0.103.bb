# Copyright (C) 2016 Joe MacDonald <joe_macdonald@mentor.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "iSNS daemon and utility programs"
DESCRIPTION = "This is a partial implementation of RFC4171, the Internet \
Storage Name Service (iSNS).  The distribution includes the iSNS server, \
supporting persisten storage of registrations, isnsadm, a command line \
utility for managing nodes, and isnsdd, a corresponding discovery daemon."
HOMEPAGE = "https://github.com/open-iscsi/open-isns/"
LICENSE = "GPL-2.0-or-later AND LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"
SECTION = "net"

DEPENDS = "openssl"

SRC_URI = "git://github.com/open-iscsi/open-isns;branch=master;protocol=https;tag=v${PV} \
           file://0001-isnsd.socket-use-run-instead-of-var-run.patch \
          "

SRCREV = "3e053e2a2c83293db7167f336940fec51f32fed7"


inherit meson systemd update-rc.d

EXTRA_OEMESON = "-Dsystemddir=${systemd_unitdir} -Drundir=${runtimedir} -Dsecurity=enabled"

do_install:append() {
    install -D -m 755 ${S}/etc/openisns.init ${D}${sysconfdir}/init.d/openisns
    sed -i 's|daemon isnsd|start-stop-daemon --start --quiet --oknodo --exec ${sbindir}/isnsd --|' \
        ${D}${sysconfdir}/init.d/openisns
}

FILES:${PN} += "${libdir} ${systemd_unitdir}"

INITSCRIPT_NAME = "openisns"
