FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Remove, from the ibm-enterprise image, the service file that starts the skeleton power
# control application. That image will use the power control application
# included in the phosphor-power repository.
OBMC_CONTROL_FMT:ibm-enterprise = ""
OBMC_CONTROL_FMT:system1 = ""
