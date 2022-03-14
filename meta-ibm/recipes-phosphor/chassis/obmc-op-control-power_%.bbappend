FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Remove, from the p10bmc image, the service file that starts the skeleton power
# control application. That image will use the power control application
# included in the phosphor-power repository.
OBMC_CONTROL_POWER_FMT:p10bmc = ""
