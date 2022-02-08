SUMMARY = "minicoredumper provides an alternate core dump facility for Linux \
to allow minimal and customized crash dumps"
LICENSE = " LGPLv2.1 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=71827c617ec7b45a0dd23658347cc1e9 \
                    file://COPYING.BSD;md5=b915ac80d5236d6aa659cb986daf00e5 \
                    file://COPYING.LGPLv2.1;md5=321bf41f280cf805086dd5a720b37785 \
                   "
DEPENDS = "elfutils dbus dbus-glib-native glib-2.0 dbus-glib util-linux json-c"

inherit autotools pkgconfig systemd update-rc.d

SRCREV = "16a0d44f1725eaa93096eaa0e086f42ef4c2712c"

PR .= "+git${SRCPV}"

SRC_URI = "git://github.com/diamon/minicoredumper;protocol=https;branch=master \
           file://minicoredumper.service \
           file://minicoredumper.init \
           "

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "minicoredumper.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "minicoredumper"
INITSCRIPT_PARAMS_${PN} = "defaults 89"

do_install_append() {
    rmdir ${D}${localstatedir}/run
    install -d ${D}/${sysconfdir}/minicoredumper
    cp -rf ${S}/etc/* ${D}/${sysconfdir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/minicoredumper.service ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/init.d
    install -m 0644 ${WORKDIR}/minicoredumper.init ${D}${sysconfdir}/init.d/minicoredumper

    # correct path of minicoredumper
    sed -i -e s:/usr/bin/minicoredumper:${sbindir}/minicoredumper:g ${D}${sysconfdir}/init.d/minicoredumper
    sed -i -e s:/usr/bin/minicoredumper:${sbindir}/minicoredumper:g ${D}${systemd_system_unitdir}/minicoredumper.service
}

# http://errors.yoctoproject.org/Errors/Details/186966/
COMPATIBLE_HOST_libc-musl = 'null'
