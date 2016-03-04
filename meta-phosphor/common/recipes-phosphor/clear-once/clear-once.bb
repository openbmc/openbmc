
SUMMARY = "Clear boot-once variables"
DESCRIPTION = "Clear u-boot variables used for one-time boot flow"

RPROVIDES_${PN} += "clear-once"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
