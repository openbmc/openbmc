# yosemite4 doesn't use xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service in power control
SOFT_SVC = ""
SOFT_TGTFMT = ""
SOFT_FMT = ""

# We do not use IPMI to manage the host, so these services are unnecessary.
# These are a set of mapper-wait calls that slow down the BMC boot.
IPMI_HOST_NEEDED_SERVICES=""
