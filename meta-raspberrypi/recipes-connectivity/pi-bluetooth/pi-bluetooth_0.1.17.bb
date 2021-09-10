SUMMARY = "Script to properly configure BT-HCI on Raspberry Pi"
HOMEPAGE = "https://github.com/RPi-Distro/pi-bluetooth"
SECTION = "kernel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "\
    file://debian/copyright;md5=6af8de3c8ee71f8e91e9b22f84ff2022 \
"

SRC_URI = "\
    git://github.com/RPi-Distro/pi-bluetooth \
    file://0001-bthelper-correct-path-for-hciconfig-under-Yocto.patch \
"
SRCREV = "fd4775bf90e037551532fc214a958074830bb80d"

S = "${WORKDIR}/git"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'update-rc.d', d)}
# hciuart.service replaces what was brcm43438.service 
SYSTEMD_SERVICE:${PN} = "\
    hciuart.service \
    bthelper@.service \
"
INITSCRIPT_NAME = "btuart"
INITSCRIPT_PARAMS = "start 18 2 3 4 5 ."

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/lib/udev/rules.d/* ${D}${sysconfdir}/udev/rules.d

    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/bthelper ${D}${bindir}
    install -m 0755 ${S}/usr/bin/btuart ${D}${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/debian/pi-bluetooth.bthelper@.service ${D}${systemd_system_unitdir}/bthelper@.service
        install -m 0644 ${S}/debian/pi-bluetooth.hciuart.service ${D}${systemd_system_unitdir}/hciuart.service
    else
        install -d ${D}${sysconfdir}/init.d/
        cat > ${WORKDIR}/btuart.init << EOF
#!/bin/sh
/usr/bin/btuart
EOF
        install -m 0755 ${WORKDIR}/btuart.init ${D}${sysconfdir}/init.d/btuart
        sed -i -e 's:TAG+="systemd".*$:RUN+="/usr/bin/bthelper %k":' ${D}${sysconfdir}/udev/rules.d/90-pi-bluetooth.rules
    fi
}

FILES:${PN} = "\
    ${bindir} \
    ${sysconfdir} \
    ${systemd_unitdir}/system \
"

RDEPENDS:${PN} += " \
    udev-rules-rpi \
"
