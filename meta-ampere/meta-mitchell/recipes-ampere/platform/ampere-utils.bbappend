FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

INSANE_SKIP:${PN} = "already-stripped"

SRC_URI:append = " \
           file://ampere_power_util.sh \
           file://ampere_power_on_driver_binder.sh \
          "

do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_power_util.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_power_on_driver_binder.sh ${D}/${sbindir}/
}
