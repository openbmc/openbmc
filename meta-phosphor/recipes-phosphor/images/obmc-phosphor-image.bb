DESCRIPTION = "Image with Phosphor, a software stack for hardware management \
in devices with baseboard management controllers.  The image supports the \
full OpenBMC feature set for devices of all types."
LICENSE = "Apache-2.0"

inherit obmc-phosphor-image

# The /etc/version file is misleading and not useful.  Remove it.
# Users should instead rely on /etc/os-release.
# Additionally set the pam login environment variables
ROOTFS_POSTPROCESS_COMMAND += "remove_etc_version ; set_pam_login_environment ;"

IMAGE_LINGUAS = ""
IMAGE_FEATURES += " \
        obmc-bmc-state-mgmt \
        obmc-bmcweb \
        obmc-chassis-mgmt \
        obmc-chassis-state-mgmt \
        obmc-console \
        obmc-devtools \
        obmc-fan-control \
        obmc-fan-mgmt \
        obmc-flash-mgmt \
        obmc-health-monitor \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-ikvm \
        obmc-inventory \
        obmc-leds \
        obmc-logging-mgmt \
        obmc-remote-logging-mgmt \
        obmc-net-ipmi \
        obmc-sensors \
        obmc-software \
        obmc-system-mgmt \
        obmc-user-mgmt \
        obmc-user-mgmt-ldap \
        ${@bb.utils.contains_any('DISTRO_FEATURES', \
            'obmc-ubi-fs phosphor-mmc obmc-static-norootfs', \
            'read-only-rootfs overlayfs-etc', '', d)} \
        ssh-server-dropbear \
        obmc-debug-collector \
        obmc-network-mgmt \
        obmc-settings-mgmt \
        obmc-telemetry \
        obmc-dmtf-pmci \
        obmc-dmtf-spdm \
        obmc-webui \
        obmc-tpm \
        "
# The shadow recipe provides the binaries(like useradd, usermod) needed by the
# phosphor-user-manager.
ROOTFS_RO_UNNEEDED:remove = "shadow"

# We need to set overlayfs-etc so that the dropbear/openssh keys don't end up
# in a volatile file system, but we always have our own init that sets these
# up.  Add enough bogus values here that rootfs-postcommands.bbclass does what
# we want without overlayfs-etc.bbclass messing things up.
OVERLAYFS_ETC_USE_ORIG_INIT_NAME = "0"
OVERLAYFS_ETC_MOUNT_POINT = "/this/is/unused"
OVERLAYFS_ETC_FSTYPE = "not_a_fs_type"
OVERLAYFS_ETC_DEVICE = "/dev/null"
python create_overlayfs_etc_preinit:append() {
    os.unlink(preinitPath)
}

# Note - this cannot be done in install:append because the file
# we are modifying comes from the shadow package, and adding a
# DEPENDS on shadow creates a circular dependency.

# 15 minutes
DEFAULT_TTY_IDLE_TIMEOUT ?= "900"

set_pam_login_environment() {
    # Modify the pam login service to support variables, specifically,
    # Add a reference to load /etc/default/pam-login-environmnt for controlling
    # autologout on inactivity
    TTYENV_STRING="session  required  pam_env.so readenv=1 envfile=/etc/default/pam-login-environment user_readenv=0"
    PAM_LOGIN_FILE="${IMAGE_ROOTFS}${sysconfdir}/pam.d/login"
    pam_tty=$(grep -v "^${TTYENV_STRING}" ${PAM_LOGIN_FILE})
    if [ -n "${pam_tty}" ]
    then
        echo >> ${PAM_LOGIN_FILE}
        echo "# Set TTY Specific variables including TMOUT" >> ${PAM_LOGIN_FILE}
        echo "${TTYENV_STRING}" >> ${PAM_LOGIN_FILE}
    fi

    # set the DEFAULT_TTY_IDLE_TIMEOUT
    PAM_LOGIN_ENV="${IMAGE_ROOTFS}${sysconfdir}/default/pam-login-environment"
    pam_env=$(grep -v "@DEFAULT_TTY_IDLE_TIMEOUT@" ${PAM_LOGIN_ENV})
    if [ -n "${pam_env}" ]
    then
        sed -i "s/@DEFAULT_TTY_IDLE_TIMEOUT@/${DEFAULT_TTY_IDLE_TIMEOUT}/" ${PAM_LOGIN_ENV}
    fi
}
