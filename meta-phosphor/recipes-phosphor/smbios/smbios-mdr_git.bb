HOMEPAGE = "http://github.com/openbmc/smbios-mdr"
SUMMARY = "Extract CPU and Memory Inventory from SMSMBIOS Table and PECI"
DESCRIPTION = "This package parses SMBIOS tables, reads Intel CPU PIROM and PECI and provides a dedicated IPMI blob to receive SMBIOS tables sent from LinuxBoot"

PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit cmake pkgconfig systemd
inherit obmc-phosphor-ipmiprovider-symlink

DEPENDS += " \
    boost \
    systemd \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    "

PACKAGECONFIG ?= "cpuinfo"
PACKAGECONFIG[smbios-no-dimm] = "-DDIMM_DBUS=OFF,-DDIMM_DBUS=ON"
PACKAGECONFIG[cpuinfo] = "-DCPU_INFO=ON,-DCPU_INFO=OFF,libpeci i2c-tools"
PACKAGECONFIG[smbios-ipmi-blob] = "-DIPMI_BLOB=ON,-DIPMI_BLOB=OFF,phosphor-ipmi-blobs"

EXTRA_OECMAKE = "-DYOCTO=ON"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/smbios-mdr.git;branch=master;protocol=https"
SRCREV = "e4ea37716d0d282ef664d5a70979274fe40e7c8b"

SYSTEMD_SERVICE:${PN} += "smbios-mdrv2.service"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'cpuinfo', 'xyz.openbmc_project.cpuinfo.service', '', d)}"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/blob-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append  = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"
BLOBIPMI_PROVIDER_LIBRARY += "${@bb.utils.contains('PACKAGECONFIG', 'smbios-ipmi-blob', 'libsmbiosstore.so', '', d)}"
