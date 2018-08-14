check_security () {
    ${STAGING_BINDIR_NATIVE}/buck-security -sysroot ${IMAGE_ROOTFS} -log ${T}/log.do_checksecurity.${PID} -disable-checks "checksum,firewall,packages_problematic,services,sshd,usermask" -no-sudo > /dev/null
}

EXTRA_IMAGEDEPENDS += "buck-security-native"

ROOTFS_POSTPROCESS_COMMAND += "check_security;"
