FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"

SYSTEMD_OVERRIDE_${PN}-updater += \
    "software-bmc-updater.conf:xyz.openbmc_project.Software.BMC.Updater.service.d/software-bmc-updater.conf"
