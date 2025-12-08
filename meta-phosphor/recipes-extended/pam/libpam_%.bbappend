FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
FILESEXTRAPATHS:prepend:df-phosphor-no-ipmi-rmcp := "${THISDIR}/${PN}-no-ipmi-rmcp:"

SRC_URI += " file://pam.d/common-password \
             file://pam.d/common-account \
             file://pam.d/common-auth \
             file://pam.d/common-session \
             file://faillock.conf \
             file://pwhistory.conf \
            "

do_install:append() {
    # The libpam recipe will always add a pam_systemd.so line to
    # common-session if systemd is enabled; however systemd only
    # builds pam_systemd.so if logind is enabled, and we disable
    # that package.  So, remove the pam_systemd.so line here.
    sed -i '/pam_systemd.so/d' ${D}${sysconfdir}/pam.d/common-session

    install -d ${D}/etc/security
    install -m 0644 ${UNPACKDIR}/faillock.conf ${D}/etc/security
    install -m 0644 ${UNPACKDIR}/pwhistory.conf ${D}/etc/security
}

RDEPENDS:${PN}-runtime += "libpwquality \
                           ${MLPREFIX}pam-plugin-faillock-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-pwhistory-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-succeed-if-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-localuser-${libpam_suffix} \
                          "
