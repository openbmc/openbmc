FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://pam.d/common-password \
             file://pam.d/common-account \
             file://pam.d/common-auth \
            "

RDEPENDS_${PN}-runtime += "${MLPREFIX}pam-plugin-cracklib-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-tally2-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-pwhistory-${libpam_suffix} \
                          "
