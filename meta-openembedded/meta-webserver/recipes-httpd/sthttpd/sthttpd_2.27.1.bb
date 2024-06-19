SUMMARY = "A simple, small, portable, fast, and secure HTTP server"
DESCRIPTION = "A simple, small, portable, fast, and secure HTTP server (supported fork of thttpd)."
HOMEPAGE = "http://opensource.dyc.edu/sthttpd"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://src/thttpd.c;beginline=1;endline=26;md5=0c5762c2c34dcbe9eb18815516502872"

DEPENDS += "base-passwd virtual/crypt"

SRCREV = "2845bf5bff2b820d2336c8c8061cbfc5f271e720"
SRC_URI = "git://github.com/blueness/${BPN};branch=master;protocol=https \
           file://0001-Define-_GNU_SOURCE-if-HAVE_SIGSET-is-set.patch \
           file://thttpd.service \
           file://thttpd.conf \
           file://init"

UPSTREAM_CHECK_URI = "https://github.com/blueness/sthttpd/releases/"
UPSTREAM_CHECK_REGEX = "v(?P<pver>\d+(\.\d+)+).tar.gz"

S = "${WORKDIR}/git"

inherit autotools update-rc.d systemd update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN}-doc = "htpasswd.1"
ALTERNATIVE_LINK_NAME[htpasswd.1] = "${mandir}/man1/htpasswd.1"

SRV_DIR ?= "${servicedir}/www"

# Note that `${sbindir}/makeweb' is installed setgid to this group,
# but ${SRV_DIR} is not installed chgrp'd to the group by default.
WEBGROUP ?= "www-data"

do_configure:prepend () {
    export WEBDIR=${SRV_DIR}
    export WEBGROUP=${WEBGROUP}
}

do_install:append () {
    install -d ${D}${sysconfdir}/init.d
    install -c -m 755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/thttpd
    install -c -m 755 ${UNPACKDIR}/thttpd.conf ${D}${sysconfdir}
    sed -i -e 's,@@CONFFILE,${sysconfdir}/thttpd.conf,g' ${D}${sysconfdir}/init.d/thttpd
    sed -i -e 's,@@SRVDIR,${SRV_DIR},g' ${D}${sysconfdir}/thttpd.conf
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/thttpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/thttpd.service ${D}${systemd_unitdir}/system
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${systemd_unitdir}/system/thttpd.service
    sed -i 's!/var/!${localstatedir}/!g' ${D}${systemd_unitdir}/system/thttpd.service
    sed -i -e 's,@@CONFFILE,${sysconfdir}/thttpd.conf,g' ${D}${systemd_unitdir}/system/thttpd.service
}

INITSCRIPT_NAME = "thttpd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE:${PN} = "thttpd.service"

FILES:${PN} += "${SRV_DIR}"
FILES:${PN}-dbg += "${SRV_DIR}/cgi-bin/.debug"

CVE_STATUS[CVE-2017-10671] = "fixed-version: No action required. The current version (2.27.1) is not affected by the CVE."
