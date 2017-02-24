SUMMARY = "A simple, small, portable, fast, and secure HTTP server"
DESCRIPTION = "A simple, small, portable, fast, and secure HTTP server (supported fork of thttpd)."
HOMEPAGE = "http://opensource.dyc.edu/sthttpd"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://src/thttpd.c;beginline=1;endline=26;md5=0c5762c2c34dcbe9eb18815516502872"

SRC_URI = "http://opensource.dyc.edu/pub/sthttpd/sthttpd-${PV}.tar.gz \
           file://thttpd.service \
           file://thttpd.conf \
           file://init"

SRC_URI[md5sum] = "f7dd2d506dc5fad2ad8794b1800d2634"
SRC_URI[sha256sum] = "97d660a881331e93818e872ce11536f461105d70a18dfc5de5895851c4b2afdb"

S = "${WORKDIR}/sthttpd-${PV}"

inherit autotools update-rc.d systemd

SRV_DIR ?= "${servicedir}/www"

# Note that `${sbindir}/makeweb' is installed setgid to this group,
# but ${SRV_DIR} is not installed chgrp'd to the group by default.
WEBGROUP ?= "www-data"

do_configure_prepend () {
    export WEBDIR=${SRV_DIR}
    export WEBGROUP=${WEBGROUP}
}

do_install_append () {
    install -d ${D}${sysconfdir}/init.d
    install -c -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/thttpd
    install -c -m 755 ${WORKDIR}/thttpd.conf ${D}${sysconfdir}
    sed -i -e 's,@@CONFFILE,${sysconfdir}/thttpd.conf,g' ${D}${sysconfdir}/init.d/thttpd
    sed -i -e 's,@@SRVDIR,${SRV_DIR},g' ${D}${sysconfdir}/thttpd.conf
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/thttpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/thttpd.service ${D}${systemd_unitdir}/system
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${systemd_unitdir}/system/thttpd.service
    sed -i 's!/var/!${localstatedir}/!g' ${D}${systemd_unitdir}/system/thttpd.service
    sed -i -e 's,@@CONFFILE,${sysconfdir}/thttpd.conf,g' ${D}${systemd_unitdir}/system/thttpd.service
}

INITSCRIPT_NAME = "thttpd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "thttpd.service"

FILES_${PN} += "${SRV_DIR}"
FILES_${PN}-dbg += "${SRV_DIR}/cgi-bin/.debug"

