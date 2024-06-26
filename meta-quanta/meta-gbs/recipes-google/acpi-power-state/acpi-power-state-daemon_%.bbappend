FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

SRC_URI:append:gbs = " \
    file://gbs-host-s0-set-failsafe.service \
    file://gbs-host-s5-set-failsafe.service \
    file://gbs-host-set-boot-failsafe@.service \
    file://gbs-check-host-state.service \
    file://gbs-set-boot-failsafe.sh \
    file://gbs-set-failsafe.sh \
    file://gbs-check-host-state.sh \
    file://gbs-host-ready.target \
    "

RDEPENDS:${PN}:append:gbs = "bash"

CHASSIS_INSTANCE="0"

SYSTEMD_SERVICE:${PN}:append:gbs = " \
    gbs-host-s0-set-failsafe.service \
    gbs-host-s5-set-failsafe.service \
    gbs-check-host-state.service \
    gbs-host-ready.target \
    "

FILES:${PN}:append:gbs = " \
    ${systemd_system_unitdir}/gbs-host-set-boot-failsafe@.service \
    "

do_install:append:gbs() {
    install -d ${D}${bindir}

    install -m 0755 ${UNPACKDIR}/gbs-set-failsafe.sh ${D}${bindir}/.
    install -m 0755 ${UNPACKDIR}/gbs-set-boot-failsafe.sh ${D}${bindir}/.
    install -m 0755 ${UNPACKDIR}/gbs-check-host-state.sh ${D}${bindir}/.

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/gbs-host-s0-set-failsafe.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/gbs-host-s5-set-failsafe.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/gbs-host-set-boot-failsafe@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/gbs-check-host-state.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/gbs-host-ready.target ${D}${systemd_system_unitdir}
}

pkg_postinst:${PN}:append() {
    mkdir -p $D$systemd_system_unitdir/gbs-host-ready.target.wants
    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/gbs-host-ready.target.wants/gbs-host-set-boot-failsafe@${i}.service"
        TARGET="../gbs-host-set-boot-failsafe@.service"
        ln -s $TARGET $LINK
    done
}
pkg_prerm:${PN}:append() {
    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/gbs-host-ready.target.wants/gbs-host-set-boot-failsafe@${i}.service"
        rm $LINK
    done
}
