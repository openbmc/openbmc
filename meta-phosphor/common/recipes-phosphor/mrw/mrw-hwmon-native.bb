SUMMARY = "Phosphor hwmon conf file generation"
DESCRIPTION = "Script to generate hwmon conf files from the MRW XML"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native
inherit mrw-rev

DEPENDS += "mrw-api-native"

SRC_URI += "${MRW_TOOLS_SRC_URI}"
SRCREV = "${MRW_TOOLS_SRCREV}"

do_install() {
    install -d ${D}${STAGING_BINDIR_NATIVE}
    install -m 0755 hwmon.pl \
        ${D}${STAGING_BINDIR_NATIVE}/hwmon.pl
}
