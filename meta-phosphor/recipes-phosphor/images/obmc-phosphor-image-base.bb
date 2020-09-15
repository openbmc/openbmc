DESCRIPTION = "A basic OpenBMC image with no features enabled."

IMAGE_LINGUAS = ""
LICENSE = "Apache-2.0"

inherit obmc-phosphor-image

# The /etc/version file is misleading and not useful.  Remove it.
# Users should instead rely on /etc/os-release.
ROOTFS_POSTPROCESS_COMMAND += "remove_etc_version ; "

# Disable the pager to prevent systemd injecting control characters into the
# output stream that are not interpreted by busybox tools.
ROOTFS_POSTPROCESS_COMMAND += "disable_systemd_pager ; "
