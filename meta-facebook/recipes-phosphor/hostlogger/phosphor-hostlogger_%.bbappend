FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:fb-compute-singlehost = " file://ttyS2.conf"
OBMC_BMC_TTY = "ttyS4"

HOST_LOGGER_INST = "${@d.getVar('OBMC_HOST_INSTANCES', True)}"
HOST_LOGGER_INST_CNT = "${@sum([1 for item in d.getVar('HOST_LOGGER_INST', True).split() if item.isdigit()])}"
HOST_LOGGER_CONFS = "${@' '.join(['file://ttyS{}.conf'.format(i) for i in range(int(d.getVar('HOST_LOGGER_INST_CNT', True)) + 1) if 'ttyS{}'.format(i) != d.getVar('OBMC_BMC_TTY', True)])}"

SRC_URI:append:fb-compute-multihost = " ${HOST_LOGGER_CONFS}"

do_install:append() {

          # Install the configurations
          install -m 0755 -d ${D}${sysconfdir}/${BPN}
          install -m 0644 ${WORKDIR}/*.conf ${D}${sysconfdir}/${BPN}/

          # Remove upstream-provided default configuration
          rm -f ${D}${sysconfdir}/${BPN}/ttyVUART0.conf
}
