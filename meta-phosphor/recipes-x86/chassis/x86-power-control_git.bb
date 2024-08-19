SUMMARY = "Chassis Power Control service for Intel based platforms"
DESCRIPTION = "Chassis Power Control service for Intel based platforms"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "a0a39f82d8299ab4959d1765d56ba614c36236eb"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/x86-power-control.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

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
  "
FILES:${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.Chassis.Control.Power@.service"

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
