FILESEXTRAPATHS_prepend_df-openpower := "${THISDIR}/${PN}:"

SYSTEMD_OVERRIDE_${PN}-updater_append_df-openpower = \
    " software-bmc-updater.conf:xyz.openbmc_project.Software.BMC.Updater.service.d/software-bmc-updater.conf"
