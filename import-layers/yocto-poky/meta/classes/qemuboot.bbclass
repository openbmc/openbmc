# Help runqemu boot target board, "QB" means Qemu Boot, the following
# vars can be set in conf files, such as <bsp.conf> to make it can be
# boot by runqemu:
#
# QB_SYSTEM_NAME: qemu name, e.g., "qemu-system-i386"
# QB_OPT_APPEND: options to append to qemu, e.g., "-show-cursor"
# QB_DEFAULT_KERNEL: default kernel to boot, e.g., "bzImage"
# QB_DEFAULT_FSTYPE: default FSTYPE to boot, e.g., "ext4"
# QB_MEM: memory, e.g., "-m 512"
# QB_MACHINE: qemu machine, e.g., "-machine virt"
# QB_CPU: qemu cpu, e.g., "-cpu qemu32"
# QB_CPU_KVM: the similar to QB_CPU, but used when kvm, e.g., '-cpu kvm64',
#             set it when support kvm.
# QB_KERNEL_CMDLINE_APPEND: options to append to kernel's -append
#                           option, e.g., "console=ttyS0 console=tty"
# QB_DTB: qemu dtb name
# QB_AUDIO_DRV: qemu audio driver, e.g., "alsa", set it when support audio
# QB_AUDIO_OPT: qemu audio option, e.g., "-soundhw ac97,es1370", used
#               when QB_AUDIO_DRV is set.
# QB_KERNEL_ROOT: kernel's root, e.g., /dev/vda
# QB_TAP_OPT: netowrk option for 'tap' mode, e.g.,
#             "-netdev tap,id=net0,ifname=@TAP@,script=no,downscript=no -device virtio-net-device,netdev=net0"
#              Note, runqemu will replace "@TAP@" with the one which is used, such as tap0, tap1 ...
# QB_SLIRP_OPT: network option for SLIRP mode, e.g.,
#             "-netdev user,id=net0 -device virtio-net-device,netdev=net0"
# QB_ROOTFS_OPT: used as rootfs, e.g.,
#               "-drive id=disk0,file=@ROOTFS@,if=none,format=raw -device virtio-blk-device,drive=disk0"
#              Note, runqemu will replace "@ROOTFS@" with the one which is used, such as core-image-minimal-qemuarm64.ext4.
# QB_SERIAL_OPT: serial port, e.g., "-serial mon:stdio"
# QB_TCPSERIAL_OPT: tcp serial port option, e.g.,
#                   " -device virtio-serial-device -chardev socket,id=virtcon,port=@PORT@,host=127.0.0.1 -device virtconsole,chardev=virtcon"
#                   Note, runqemu will replace "@PORT@" with the port number which is used.
#
# Usage:
# IMAGE_CLASSES += "qemuboot"
# See "runqemu help" for more info

QB_MEM ?= "-m 256"
QB_SERIAL_OPT ?= "-serial mon:stdio -serial null"
QB_DEFAULT_KERNEL ?= "${KERNEL_IMAGETYPE}"
QB_DEFAULT_FSTYPE ?= "ext4"
QB_OPT_APPEND ?= "-show-cursor"

# Create qemuboot.conf
ROOTFS_POSTPROCESS_COMMAND += "write_qemuboot_conf; "

python write_qemuboot_conf() {
    import configparser

    build_vars = ['MACHINE', 'TUNE_ARCH', 'DEPLOY_DIR_IMAGE', \
                'KERNEL_IMAGETYPE', 'IMAGE_NAME', 'IMAGE_LINK_NAME', \
                'STAGING_DIR_NATIVE', 'STAGING_BINDIR_NATIVE', \
                'STAGING_DIR_HOST']

    # Vars from bsp
    qb_vars = []
    for k in d.keys():
        if k.startswith('QB_'):
            qb_vars.append(k)

    qemuboot = "%s/%s.qemuboot.conf" % (d.getVar('DEPLOY_DIR_IMAGE', True), d.getVar('IMAGE_NAME', True))
    qemuboot_link = "%s/%s.qemuboot.conf" % (d.getVar('DEPLOY_DIR_IMAGE', True), d.getVar('IMAGE_LINK_NAME', True))
    cf = configparser.ConfigParser()
    cf.add_section('config_bsp')
    for k in build_vars + qb_vars:
        cf.set('config_bsp', k, '%s' % d.getVar(k, True))

    # QB_DEFAULT_KERNEL's value of KERNEL_IMAGETYPE is the name of a symlink
    # to the kernel file, which hinders relocatability of the qb conf.
    # Read the link and replace it with the full filename of the target.
    kernel_link = os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), d.getVar('QB_DEFAULT_KERNEL', True))
    kernel = os.path.realpath(kernel_link)
    cf.set('config_bsp', 'QB_DEFAULT_KERNEL', kernel)

    bb.utils.mkdirhier(os.path.dirname(qemuboot))
    with open(qemuboot, 'w') as f:
        cf.write(f)

    if os.path.lexists(qemuboot_link):
       os.remove(qemuboot_link)
    os.symlink(os.path.basename(qemuboot), qemuboot_link)
}
