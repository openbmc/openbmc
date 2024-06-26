FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://pam.d/common-password \
             file://pam.d/common-account \
             file://pam.d/common-auth \
             file://pam.d/common-session \
             file://faillock.conf \
             file://pwhistory.conf \
             file://convert-pam-configs.service \
             file://convert-pam-configs.sh \
            "

inherit systemd
SYSTEMD_SERVICE:${PN} += "convert-pam-configs.service"

FILES:${PN} += "${bindir}/convert-pam-configs.sh \
                ${systemd_system_unitdir}/convert-pam-configs.service \
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

    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/convert-pam-configs.sh ${D}${bindir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/convert-pam-configs.service ${D}${systemd_system_unitdir}
}

RDEPENDS:${PN}-runtime += "libpwquality \
                           ${MLPREFIX}pam-plugin-faillock-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-pwhistory-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-succeed-if-${libpam_suffix} \
                           ${MLPREFIX}pam-plugin-localuser-${libpam_suffix} \
                          "

#
# Background:
# 1. Linux-PAM modules tally2 and cracklib were removed in libpam_1.5,
# which prompted OpenBMC to change to the faillock and pwquality modules.
# The PAM config files under /etc/pam.d were changed accordingly.
# 2. OpenBMC implementations store Redfish property values in PAM config files.
# For example, the D-Bus property maxLoginAttemptBeforeLockout is stored in
# /etc/pam.d/common-auth as the pam_tally2.so deny= parameter value.
# 3. The /etc directory is readonly and has a readwrite overlayfs.  That
# means when a config file changes, an overlay file is created which hides
# the readonly version.
#
# Problem scenario:
# 1. Begin with a BMC that has a firmware image which has the old PAM
# modules and the old PAM config files which have modified parameters.
# For example, there is an overlay file for /etc/pam.d/common-auth.
# 2. Perform a firmware update to a firmware image which has the new PAM
# modules.  The updated image will have not have the old PAM modules.
# It will have the new PAM config files in its readonly file system and
# the old PAM config files in its readwrite overlay.
# 3. Note that PAM authentication will always fail at this point because
# the old PAM config files in the overlay tell PAM to use the old PAM
# modules which are not present on the system.
#
# Two possible recoveries are:
# A. Factory reset the BMC.  This will clear the readwrite overlay,
# allowing PAM to use the readonly version.
# B. Convert the old PAM config files to the new style.  See below.
#
# Service: The convert-pam-configs.service updates the old-style PAM config
# files on the BMC: it changes uses of the old modules to the new modules
# and carries forward configuration parameters.  A key point is that files
# are written to *only* as needed to convert uses of the old modules to the
# new modules.  See the conversion tool for details.
#
# This service can be removed when the BMC no longer supports a direct
# firware update path from a version which has the old PAM configs to a
# version which has the new PAM configs.
#
# In case of downgrade, Factory reset is recommended. Current logic in existing
# images won't be able to take care of these settings during downgrade.
