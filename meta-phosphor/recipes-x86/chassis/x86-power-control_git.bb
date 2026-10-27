SUMMARY = "Chassis Power Control service for Intel based platforms"
DESCRIPTION = "Chassis Power Control service for Intel based platforms"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "bba731314b6834d0b3263d0b72d936fe8646ee6c"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/x86-power-control.git;protocol=https;branch=master"

inherit meson systemd pkgconfig
inherit obmc-phosphor-dbus-service

SYSTEMD_SERVICE:${PN} += "chassis-system-reset.service \
                         chassis-system-reset.target"
DEPENDS += " \
    boost \
    i2c-tools \
    libgpiod \
    nlohmann-json \
    sdbusplus \
    phosphor-logging \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gmock', '', d)} \
  "
FILES:${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.Chassis.Control.Power@.service"

EXTRA_OEMESON:append = " \
    -Dtests=${@bb.utils.contains('PTEST_ENABLED', '1', 'enabled', 'disabled', d)} \
"

pkg_postinst:${PN}:append() {
    mkdir -p $D$systemd_system_unitdir/sysinit.target.wants
    for i in ${OBMC_HOST_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/sysinit.target.wants/xyz.openbmc_project.Chassis.Control.Power@${i}.service"
        TARGET="../xyz.openbmc_project.Chassis.Control.Power@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/sysinit.target.requires/xyz.openbmc_project.Chassis.Control.Power@${i}.service"
        rm $LINK
    done
}
