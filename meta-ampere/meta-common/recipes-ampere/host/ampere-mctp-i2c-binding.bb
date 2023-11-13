SUMMARY = "Ampere Mctp Control service"
DESCRIPTION = "Handle MCTP-PLDM i2c binding for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} = "bash"
FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI = " \
           file://ampere-mctp-i2c-binding.service \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "ampere-mctp-i2c-binding.service"

AMPERE_MCTP_I2C_BINDING_TGT = "ampere-mctp-i2c-binding.service"
AMPERE_MCTP_I2C_BINDING_INSTMPL = "ampere-mctp-i2c-binding.service"
AMPERE_HOST_RUNNING = "obmc-host-already-on@{0}.target"
AMPERE_MCTP_I2C_BINDING_TARGET_FMT = "../${AMPERE_MCTP_I2C_BINDING_TGT}:${AMPERE_HOST_RUNNING}.wants/${AMPERE_MCTP_I2C_BINDING_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'AMPERE_MCTP_I2C_BINDING_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"
