# host watchdog does not support on bletchley
RDEPENDS:${PN}:remove = "phosphor-watchdog"

# bletchley doesn't have IPMI support, skip xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service
SOFT_SVC = ""
SOFT_TGTFMT = ""
SOFT_FMT = ""
