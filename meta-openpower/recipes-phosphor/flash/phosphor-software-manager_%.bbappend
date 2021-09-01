FILESEXTRAPATHS:prepend:df-openpower := "${THISDIR}/${PN}:"

SYSTEMD_OVERRIDE:${PN}-updater:append:df-openpower = \
    " software-bmc-updater.conf:xyz.openbmc_project.Software.BMC.Updater.service.d/software-bmc-updater.conf"
