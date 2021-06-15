# This is needed to guarantee that our whitelist takes
# precendence over the OpenBMC one
FILESEXTRAPATHS_prepend_gbmc := "${THISDIR}/${PN}:"

SRC_URI_append_gbmc_dev = " file://whitelist.dev"

do_install_append_gbmc_dev() {
    cat ${WORKDIR}/whitelist.dev >>${D}/whitelist
}

SRC_URI_append_gbmc_dev = " file://rwfs-clean-dev.patch"
# Required for the clean-dev patch to detect version changes
RDEPENDS_${PN}_append_gbmc_dev = " os-release"

do_install_append_gbmc_prod() {
    echo "clean-rwfs-filesystem" > ${D}/init-options-base
    chmod 0644 ${D}/init-options-base
}

FILES_${PN}_append_gbmc_prod = " /init-options-base"
