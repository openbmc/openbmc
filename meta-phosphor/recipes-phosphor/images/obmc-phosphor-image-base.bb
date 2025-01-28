DESCRIPTION = "A basic OpenBMC image with no features enabled."
LICENSE = "Apache-2.0"

inherit obmc-phosphor-image

# The /etc/version file is misleading and not useful.  Remove it.
# Users should instead rely on /etc/os-release.
ROOTFS_POSTPROCESS_COMMAND += "remove_etc_version"

IMAGE_LINGUAS = ""
