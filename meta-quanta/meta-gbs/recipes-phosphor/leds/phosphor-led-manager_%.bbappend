FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://service-override.conf"

SYSTEMD_OVERRIDE_${PN}-ledmanager_append_gbs = " \
  service-override.conf:xyz.openbmc_project.LED.GroupManager.service.d/service-override.conf"
