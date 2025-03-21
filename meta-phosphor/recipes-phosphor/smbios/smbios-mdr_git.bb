SUMMARY = "Extract CPU and Memory Inventory from SMSMBIOS Table and PECI"
DESCRIPTION = "This package parses SMBIOS tables, reads Intel CPU PIROM and PECI and provides a dedicated IPMI blob to receive SMBIOS tables sent from LinuxBoot"
HOMEPAGE = "http://github.com/openbmc/smbios-mdr"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += " \
    boost \
    systemd \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    "
SRCREV = "5af42bbf374907888c09d7aaf2cbf74d74c7707d"
PACKAGECONFIG[smbios-no-dimm] = "-Ddimm-dbus=disabled,-Ddimm-dbus=enabled"
PACKAGECONFIG[cpuinfo] = "-Dcpuinfo=enabled,-Dcpuinfo=disabled,i2c-tools"
PACKAGECONFIG[cpuinfo-peci] = "-Dcpuinfo-peci=enabled,-Dcpuinfo-peci=disabled,libpeci"
PACKAGECONFIG[smbios-ipmi-blob] = "-Dsmbios-ipmi-blob=enabled,-Dsmbios-ipmi-blob=disabled,phosphor-ipmi-blobs"
PACKAGECONFIG[assoc-trim-path] = "-Dassoc-trim-path=enabled,-Dassoc-trim-path=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/smbios-mdr.git;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "smbios-mdrv2.service"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'cpuinfo', 'xyz.openbmc_project.cpuinfo.service', '', d)}"

inherit meson pkgconfig systemd

FILES:${PN} += "${libdir}/blob-ipmid"
