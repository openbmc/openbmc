SUMMARY = "Clear boot-once variables"
DESCRIPTION = "Clear u-boot variables used for one-time boot flow"

RDEPENDS_${PN} = "${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or 'u-boot-fw-utils'}"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
