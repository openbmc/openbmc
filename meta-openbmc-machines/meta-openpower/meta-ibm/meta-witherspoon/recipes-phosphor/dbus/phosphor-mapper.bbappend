FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

MAPPER_SVC = "xyz.openbmc_project.ObjectMapper.service"

MAPPER_DROPIN_DIR = "${MAPPER_SVC}.d"

SYSTEMD_OVERRIDE_${PN} += "mapper-nice.conf:${MAPPER_DROPIN_DIR}/mapper-nice.conf"
