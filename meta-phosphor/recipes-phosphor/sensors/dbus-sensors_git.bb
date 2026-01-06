SUMMARY = "dbus-sensors"
DESCRIPTION = "Dbus Sensor Services Configured from D-Bus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS = " \
    boost \
    i2c-tools \
    libgpiod \
    liburing \
    nlohmann-json \
    phosphor-logging \
    sdbusplus \
    "
SRCREV = "d85c89518604abc63f9f0121f334277234271dfb"
PACKAGECONFIG ??= " \
    adcsensor \
    exitairtempsensor \
    external \
    fansensor \
    hwmontempsensor \
    intelcpusensor \
    intrusionsensor \
    ipmbsensor \
    mcutempsensor \
    psusensor \
    "
PACKAGECONFIG:append:df-mctp = "\
    mctpreactor \
    "
PACKAGECONFIG[adcsensor] = "-Dadc=enabled, -Dadc=disabled"
PACKAGECONFIG[cablemonitor] = "-Dcable-monitor=enabled, -Dcable-monitor=disabled"
PACKAGECONFIG[exitairtempsensor] = "-Dexit-air=enabled, -Dexit-air=disabled"
PACKAGECONFIG[external] = "-Dexternal=enabled, -Dexternal=disabled"
PACKAGECONFIG[fansensor] = "-Dfan=enabled, -Dfan=disabled"
PACKAGECONFIG[hwmontempsensor] = "-Dhwmon-temp=enabled, -Dhwmon-temp=disabled"
PACKAGECONFIG[intelcpusensor] = "-Dintel-cpu=enabled, -Dintel-cpu=disabled, libpeci"
PACKAGECONFIG[intrusionsensor] = "-Dintrusion=enabled, -Dintrusion=disabled"
PACKAGECONFIG[ipmbsensor] = "-Dipmb=enabled, -Dipmb=disabled"
PACKAGECONFIG[leakdetector] = "-Dleakdetector=enabled, -Dleakdetector=disabled"
PACKAGECONFIG[mctpreactor] = "-Dmctp=enabled, -Dmctp=disabled"
PACKAGECONFIG[mcutempsensor] = "-Dmcu=enabled, -Dmcu=disabled"
PACKAGECONFIG[nvidia-gpu] = "-Dnvidia-gpu=enabled, -Dnvidia-gpu=disabled"
PACKAGECONFIG[nvmesensor] = "-Dnvme=enabled, -Dnvme=disabled"
PACKAGECONFIG[psusensor] = "-Dpsu=enabled, -Dpsu=disabled"
PACKAGECONFIG[smbpbi] = "-Dsmbpbi=enabled, -Dsmbpbi=disabled"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/dbus-sensors.git;branch=master;protocol=https"

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'adcsensor', \
                                               'xyz.openbmc_project.adcsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', \
                                               'xyz.openbmc_project.cablemonitor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'exitairtempsensor', \
                                               'xyz.openbmc_project.exitairsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'external', \
                                               'xyz.openbmc_project.externalsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'fansensor', \
                                               'xyz.openbmc_project.fansensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'hwmontempsensor', \
                                               'xyz.openbmc_project.hwmontempsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'intelcpusensor', \
                                               'xyz.openbmc_project.intelcpusensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'intrusionsensor', \
                                               'xyz.openbmc_project.intrusionsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'ipmbsensor', \
                                               'xyz.openbmc_project.ipmbsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'leakdetector', \
                                               'xyz.openbmc_project.leakdetector.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'mctpreactor', \
                                               'xyz.openbmc_project.mctpreactor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'mcutempsensor', \
                                               'xyz.openbmc_project.mcutempsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'nvidia-gpu', \
                                               'xyz.openbmc_project.nvidiagpusensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'nvmesensor', \
                                               'xyz.openbmc_project.nvmesensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'psusensor', \
                                               'xyz.openbmc_project.psusensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'smbpbi', \
                                               'xyz.openbmc_project.smbpbisensor.service', \
                                               '', d)}"

S = "${WORKDIR}/git"

inherit pkgconfig meson systemd

EXTRA_OEMESON:append = " -Dtests=disabled"
