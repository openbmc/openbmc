FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " file://ampere_mctp_i2c_binding.sh"

do_install:append () {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/ampere_mctp_i2c_binding.sh ${D}/${sbindir}/
}
