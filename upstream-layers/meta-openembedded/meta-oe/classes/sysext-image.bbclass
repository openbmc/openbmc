#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# System extension images may – dynamically at runtime — extend the
# /usr/ and /opt/ directory hierarchies with additional files. This is
# particularly useful on immutable system images where a /usr/ and/or
# /opt/ hierarchy residing on a read-only file system shall be
# extended temporarily at runtime without making any persistent
# modifications.

## Example usage:
# extension-image-example.bb
#SUMMARY = "An example image to showcase a system extension image."
#LICENSE = "MIT"
#inherit discoverable-disk-image sysext-image
#IMAGE_FEATURES = ""
#IMAGE_LINGUAS = ""
#IMAGE_INSTALL = "gdb"
#
## After building, the resulting 'extension-image-example-*sysext.rootfs.ddi'
# can be deployed to an embedded system (running from a RO rootfs) and
# 'merged' into the OS by following steps:
## 1. place a symlink into the systemd-sysext image search path:
# $> mkdir /run/extensions
# $> ln -s /tmp/extension-example.sysext.ddi /run/extensions/example.raw
## 2. list all available extensions:
# $> systemd-sysext list
## 3. and enable the found extensions:
# $> SYSTEMD_LOG_LEVEL=debug systemd-sysext merge

# Note: PACKAGECONFIG:pn-systemd needs to include 'sysext'

# systemd-sysext [1] has a simple mechanism for version compatibility:
# the extension to be loaded has to contain a file named
# /usr/lib/extension-release.d/extension-release.NAME
# with "NAME" part *exactly* matching the filename of the extensions
# raw-device filename/
#
# From the extension-release file the "ID" and "VERSION_ID" fields are
# matched against same fields present in `os-release` and the extension
# is "merged" only if values in both fields from both files are an
# exact match.
#
# Link: https://www.freedesktop.org/software/systemd/man/latest/systemd-sysext.html

inherit image

# Include '.sysext' in the deployed image filename and symlink
IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_MACHINE_SUFFIX}${IMAGE_VERSION_SUFFIX}.sysext"
IMAGE_LINK_NAME = "${IMAGE_BASENAME}${IMAGE_MACHINE_SUFFIX}.sysext"
EXTENSION_NAME = "${IMAGE_LINK_NAME}.${IMAGE_FSTYPES}"

# Base extension identification fields
EXTENSION_ID_FIELD ?= "${DISTRO}"
EXTENSION_VERSION_FIELD ?= "${DISTRO_VERSION}"

sysext_image_add_version_identifier_file() {
    # Use matching based on Distro name and version
    echo 'ID=${EXTENSION_ID_FIELD}' > ${WORKDIR}/extension-release.base
    # os-release.bb does "sanitise_value(ver)", which needs to be done here too
    echo 'VERSION_ID=${EXTENSION_VERSION_FIELD}' \
        | sed 's,+,-,g;s, ,_,g' \
        >> ${WORKDIR}/extension-release.base

    # Instruct `systemd-sysext` to perform re-load once extension image is verified
    echo 'EXTENSION_RELOAD_MANAGER=1' >> ${WORKDIR}/extension-release.base

    install -d ${IMAGE_ROOTFS}${nonarch_libdir}/extension-release.d
    install -m 0644 ${WORKDIR}/extension-release.base \
        ${IMAGE_ROOTFS}${nonarch_libdir}/extension-release.d/extension-release.${EXTENSION_NAME}

    # systemd-sysext expects an extension-release file of the exact same name as the image;
    # by setting a xattr we allow renaming of the extension image file.
    # (Kernel: this requires xattr support in the used filesystem)
    setfattr -n user.extension-release.strict -v false \
        ${IMAGE_ROOTFS}${nonarch_libdir}/extension-release.d/extension-release.${EXTENSION_NAME}
}

ROOTFS_POSTPROCESS_COMMAND += "sysext_image_add_version_identifier_file"

# remove 'os-release' from the packages to be installed into the image.
# systemd-sysext otherwise raises the error:
# Extension contains '/usr/lib/os-release', which is not allowed, refusing.
PACKAGE_EXCLUDE += "os-release"
