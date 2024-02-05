DEPENDS:append = " bletchley-yaml-config"

EXTRA_OEMESON = " \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/bletchley-yaml-config/ipmi-fru-read.yaml \
    "

# host watchdog does not support on bletchley
RDEPENDS:${PN}:remove = "phosphor-watchdog"

# bletchley doesn't have IPMI support, skip xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service
SOFT_SVC = ""
SOFT_TGTFMT = ""
SOFT_FMT = ""
