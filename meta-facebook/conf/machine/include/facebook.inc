DISTROOVERRIDES .= ":facebook"

require conf/distro/include/cgroup.inc
require conf/distro/include/phosphor-no-webui.inc
require conf/distro/include/phosphor-production.inc

MACHINE_FEATURES += "\
        obmc-bmc-state-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-fb-apps"

IMAGE_FEATURES:remove = "obmc-ikvm"
IMAGE_FEATURES:append = " allow-root-login"

# Most of our hardware uses the default ttyS4 but at 57600.
SERIAL_CONSOLES:facebook = "57600;ttyS4"

OBMC_ORG_YAML_SUBDIRS:append:facebook = " com/meta"

# Remove some common features we have no use for.
DISTRO_FEATURES:remove = "avahi"
DISTRO_FEATURES:remove = "ldap"
