#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This class installs additional files found on the build host
# directly into the rootfs.
#
# One use case is to install a constant ssh host key in
# an image that gets created for just one machine. This
# solves two issues:
# - host key generation on the device can stall when the
#   kernel has not gathered enough entropy yet (seen in practice
#   under qemu)
# - ssh complains by default when the host key changes
#
# For dropbear, with the ssh host key store along side the local.conf:
# 1. Extend local.conf:
#    INHERIT += "rootfsdebugfiles"
#    ROOTFS_DEBUG_FILES += "${TOPDIR}/conf/dropbear_rsa_host_key ${IMAGE_ROOTFS}/etc/dropbear/dropbear_rsa_host_key ;"
# 2. Boot the image once, copy the dropbear_rsa_host_key from
#    the device into your build conf directory.
# 3. A optional parameter can be used to set file mode
#    of the copied target, for instance:
#    ROOTFS_DEBUG_FILES += "${TOPDIR}/conf/dropbear_rsa_host_key ${IMAGE_ROOTFS}/etc/dropbear/dropbear_rsa_host_key 0600;"
#    in case they might be required to have a specific mode. (Shoundn't be too open, for example)
#
# Do not use for production images! It bypasses several
# core build mechanisms (updating the image when one
# of the files changes, license tracking in the image
# manifest, ...).

ROOTFS_DEBUG_FILES ?= ""
ROOTFS_DEBUG_FILES[doc] = "Lists additional files or directories to be installed with 'cp -a' in the format 'source1 target1;source2 target2;...'"

ROOTFS_POSTPROCESS_COMMAND += "rootfs_debug_files"
rootfs_debug_files () {
   #!/bin/sh -e
   echo "${ROOTFS_DEBUG_FILES}" | sed -e 's/;/\n/g' | while read source target mode; do
      if [ -e "$source" ]; then
         mkdir -p $(dirname $target)
         cp -a $source $target
         [ -n "$mode" ] && chmod $mode $target
      fi
   done
}
