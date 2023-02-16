# Recipe for building userspace part of USB/IP
#
# Started with work from chuck kamas - 2021-11-05
# https://lists.yoctoproject.org/g/yocto/topic/86249103?p=,,,20,0,0,0::recentpostdate/sticky,,,20,0,0,86249103
# Though have rewritten all the logic to be much simpler
#
# SPDX-License-Identifier: MIT
#
# Author(s)
#   clst@ambu.com (Claus Stovgaard)
#

SUMMARY = "userspace usbip from Linux kernel tools"
DESCRIPTION = " USB/IP protocol allows to pass USB device from server to \
client over the network. Server is a machine which provides (shares) a \
USB device. Client is a machine which uses USB device provided by server \
over the network. The USB device may be either physical device connected \
to a server or software entity created on a server using USB gadget subsystem."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
DEPENDS = "virtual/kernel udev"
PROVIDES = "virtual/usbip-tools"

inherit kernelsrc autotools-brokensep

do_configure[depends] += "virtual/kernel:do_shared_workdir"

# We need to set S, for not being set to STAGING_KERNEL_DIR, and by that
# be wiped when we prune dest below. We just set it to usbip-tools-1.0
S = "${WORKDIR}/${BP}"

# Copy the source files from KERNEL/tools/usb/usbip to ${S}
do_configure[prefuncs] += "copy_usbip_source_from_kernel"
python copy_usbip_source_from_kernel() {
    dir_in_kernel = "tools/usb/usbip"
    src_dir = d.getVar("STAGING_KERNEL_DIR")
    src = oe.path.join(src_dir, dir_in_kernel)
    dest = d.getVar("S")
    bb.utils.mkdirhier(dest)
    bb.utils.prunedir(dest)
    # copy src to dest folder
    if not os.path.exists(src):
        bb.fatal("Path does not exist: %s. Maybe dir_in_kernel does not match the kernel version." % src)
    if os.path.isdir(src):
        oe.path.copyhardlinktree(src, dest)
    else:
        src_path = os.path.dirname(src)
        os.makedirs(os.path.join(dest,src_path),exist_ok=True)
        bb.utils.copyfile(src, dest)
}

# Use local scripts before relying on inherited autotools
do_configure () {
    # We are in ${B} - equal to ${S}, so just run the scripts
    ./cleanup.sh || bbnote "${PN} failed to cleanup.sh"
    ./autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

# As usbip integrate with the kernel module, we set this package to be build specific for
# this machine, and not generally for the architecture
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Even though the libusbip is set to version 0.0.1, set the package version to match kernel
# e.g. usbip-tools-5.14.21-r0.qemux86_64.rpm for qemu package using kernel 5.14.21
python do_package:prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION").split("-")[0])
}