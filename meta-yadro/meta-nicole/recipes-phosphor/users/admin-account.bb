SUMMARY = "Default administrative account"
DESCRIPTION = "Creating default account for system administrator"
PR = "r1"

inherit useradd

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Dependencies
DEPENDS = "sudo \
           phosphor-ipmi-host \
           phosphor-user-manager"

# Groups
GROUP_ADMIN = "priv-admin"
GROUP_OPERATOR = "priv-operator"
GROUP_USER = "priv-user"
GROUP_IPMI = "ipmi"

# Default administrative account (login: admin, password: admin)
ADMIN_LOGIN = "admin"
ADMIN_PASSW = "\$1\$Fze0kFe8\$sylEANC01t.osF8OewyB/1"
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--groups ${GROUP_ADMIN},${GROUP_IPMI} \
                       --password '${ADMIN_PASSW}' \
                       ${ADMIN_LOGIN}"

# We don't have package body
ALLOW_EMPTY_${PN} = "1"

# Workaround for meta-phosphor/classes/phosphor-rootfs-postcommands.bbclass.
# The bb-script cannot add root to non-empty groups (invalid sed command).
GROUPMEMS_PARAM_${PN} = "-a root -g ${GROUP_ADMIN}; \
                         -a root -g ${GROUP_IPMI}"
