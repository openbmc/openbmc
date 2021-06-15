# Copyright (C) 2016 Joe MacDonald <joe_macdonald@mentor.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "iSNS daemon and utility programs"
DESCRIPTION = "This is a partial implementation of RFC4171, the Internet \
Storage Name Service (iSNS).  The distribution includes the iSNS server, \
supporting persisten storage of registrations, isnsadm, a command line \
utility for managing nodes, and isnsdd, a corresponding discovery daemon."
HOMEPAGE = "http://github.com/gonzoleeman/open-isns/"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"
SECTION = "net"

DEPENDS = "openssl"

SRC_URI = "git://github.com/open-iscsi/open-isns \
           file://0001-isnsd.socket-use-run-instead-of-var-run.patch \
           "

SRCREV = "0d86dc31fae2e2d77a082ccea5aba95426b40c3c"

S = "${WORKDIR}/git"

inherit systemd autotools-brokensep update-rc.d

EXTRA_OECONF = " --prefix=${prefix} --enable-shared"
EXTRA_OEMAKE += "SYSTEMDDIR=${D}${systemd_unitdir}/system"

do_install_append () {
    oe_runmake INCDIR=${D}${includedir}/libisns/ install_hdrs
    oe_runmake LIBDIR=${D}${libdir} install_lib

    install -D -m 755 ${S}/etc/openisns.init ${D}${sysconfdir}/init.d/openisns
    sed -i 's|daemon isnsd|start-stop-daemon --start --quiet --oknodo --exec ${sbindir}/isnsd --|' \
        ${D}${sysconfdir}/init.d/openisns
}

FILES_${PN} += "${libdir} ${systemd_unitdir}"

INITSCRIPT_NAME = "openisns"
