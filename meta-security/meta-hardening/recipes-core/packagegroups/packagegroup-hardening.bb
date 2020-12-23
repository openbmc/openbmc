#
# 
#

SUMMARY = "Hardening example group"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}  \
    packagegroup-${PN} \
"

RDEPENDS_${PN} = "\
    init-ifupdown \
    ${VIRTUAL-RUNTIME_base-utils-syslog} \
    sudo \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "pam-plugin-wheel", "",d)} \
"
