FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:fb-withhost = " file://set-bmc-time-from-host"


RDEPENDS:${PN}:append:fb-withhost = " bash"

do_install:append:fb-withhost(){

    # Store the bitbake variable OBMC_HOST_INSTANCES  inside time sync script as HOST_INSTANCES variable using sed.
    sed -i -e "s,HOST_INSTANCES_SED_REPLACEMENT_VALUE,${OBMC_HOST_INSTANCES},g" ${WORKDIR}/set-bmc-time-from-host

    install -d ${D}$/lib/systemd/system
    install -m 0644 ${WORKDIR}/bmc-set-time.service  ${D}$/lib/systemd/system
    install -d ${D}/usr/libexec
    install -m 0777 ${WORKDIR}/set-bmc-time-from-host ${D}/usr/libexec
}

SYSTEMD_SERVICE:${PN}:fb-withhost += "bmc-set-time.service"
