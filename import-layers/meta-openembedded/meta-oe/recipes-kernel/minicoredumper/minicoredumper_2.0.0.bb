SUMMARY = "minicoredumper provides an alternate core dump facility for Linux \
to allow minimal and customized crash dumps"
LICENSE = " LGPLv2.1 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=aa846ae365f71b69e9fa0d21a56391ec \
                    file://COPYING.BSD;md5=b915ac80d5236d6aa659cb986daf00e5 \
                    file://COPYING.LGPLv2.1;md5=321bf41f280cf805086dd5a720b37785 \
                   "
DEPENDS = "elfutils dbus dbus-glib-native glib-2.0 dbus-glib util-linux json-c"

inherit autotools pkgconfig systemd update-rc.d

SRCREV = "cdc1faf1212a51e89b477468876e229223fceb9a"

PR .= "+git${SRCPV}"

SRC_URI = "git://git.linuxfoundation.org/diamon/minicoredumper.git;protocol=http \
           file://minicoredumper.service \
           file://minicoredumper.init \
           file://0001-minicoredumper-Initialize-pointer-to-config-struct-t.patch \
           file://0001-fix-compile-error-on-arch-x32.patch \
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
}
