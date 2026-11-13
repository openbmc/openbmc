SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://py-smbus/smbusmodule.c;beginline=1;endline=13;md5=66de33a36142d8c0e706898f7d05b3fd"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/i2c-tools-${PV}.tar.gz"
SRC_URI[sha256sum] = "04d1e3b0cd88df8fb96e7709f374dd0b3561191b4c0363eaf873a074b8b7cb22"

DEPENDS += "i2c-tools"

S = "${UNPACKDIR}/i2c-tools-${PV}"

inherit setuptools3

SETUPTOOLS_SETUP_PATH = "${S}/py-smbus"
