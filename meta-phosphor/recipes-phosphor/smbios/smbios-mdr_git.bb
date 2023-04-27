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
SRCREV = "defbc2acdbdf7827706ddd7fee53d193b3d849b3"
PACKAGECONFIG ?= "cpuinfo"
PACKAGECONFIG[smbios-no-dimm] = "-DDIMM_DBUS=OFF,-DDIMM_DBUS=ON"
PACKAGECONFIG[cpuinfo] = "-DCPU_INFO=ON,-DCPU_INFO=OFF,libpeci i2c-tools"
PACKAGECONFIG[smbios-ipmi-blob] = "-DIPMI_BLOB=ON,-DIPMI_BLOB=OFF,phosphor-ipmi-blobs"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/smbios-mdr.git;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "smbios-mdrv2.service"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'cpuinfo', 'xyz.openbmc_project.cpuinfo.service', '', d)}"

inherit cmake pkgconfig systemd
inherit obmc-phosphor-ipmiprovider-symlink

EXTRA_OECMAKE = "-DYOCTO=ON"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/blob-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

BLOBIPMI_PROVIDER_LIBRARY += "${@bb.utils.contains('PACKAGECONFIG', 'smbios-ipmi-blob', 'libsmbiosstore.so', '', d)}"
