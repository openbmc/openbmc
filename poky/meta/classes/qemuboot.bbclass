# Help runqemu boot target board, "QB" means Qemu Boot, the following
# vars can be set in conf files, such as <bsp.conf> to make it can be
# boot by runqemu:
#
# QB_SYSTEM_NAME: qemu name, e.g., "qemu-system-i386"
#
# QB_OPT_APPEND: options to append to qemu, e.g., "-show-cursor"
#
# QB_DEFAULT_KERNEL: default kernel to boot, e.g., "bzImage"
#
# QB_DEFAULT_FSTYPE: default FSTYPE to boot, e.g., "ext4"
#
# QB_MEM: memory, e.g., "-m 512"
#
# QB_MACHINE: qemu machine, e.g., "-machine virt"
#
# QB_CPU: qemu cpu, e.g., "-cpu qemu32"
#
# QB_CPU_KVM: the similar to QB_CPU, but used when kvm, e.g., '-cpu kvm64',
#             set it when support kvm.
#
# QB_KERNEL_CMDLINE_APPEND: options to append to kernel's -append
#                           option, e.g., "console=ttyS0 console=tty"
#
# QB_DTB: qemu dtb name
#
# QB_AUDIO_DRV: qemu audio driver, e.g., "alsa", set it when support audio
#
# QB_AUDIO_OPT: qemu audio option, e.g., "-soundhw ac97,es1370", used
#               when QB_AUDIO_DRV is set.
#
# QB_KERNEL_ROOT: kernel's root, e.g., /dev/vda
#
# QB_NETWORK_DEVICE: network device, e.g., "-device virtio-net-pci,netdev=net0,mac=@MAC@",
#                    it needs work with QB_TAP_OPT and QB_SLIRP_OPT.
#                    Note, runqemu will replace @MAC@ with a predefined mac, you can set
#                    a custom one, but that may cause conflicts when multiple qemus are
#                    running on the same host.
#                    Note: If more than one interface of type -device virtio-net-device gets added,
#                          QB_NETWORK_DEVICE_prepend might be used, since Qemu enumerates the eth*
#                          devices in reverse order to -device arguments.
#
# QB_TAP_OPT: network option for 'tap' mode, e.g.,
#             "-netdev tap,id=net0,ifname=@TAP@,script=no,downscript=no"
#              Note, runqemu will replace "@TAP@" with the one which is used, such as tap0, tap1 ...
#
# QB_SLIRP_OPT: network option for SLIRP mode, e.g., -netdev user,id=net0"
#
# QB_CMDLINE_IP_SLIRP: If QB_NETWORK_DEVICE adds more than one network interface to qemu, usually the
#                      ip= kernel comand line argument needs to be changed accordingly. Details are documented
#                      in the kernel docuemntation https://www.kernel.org/doc/Documentation/filesystems/nfs/nfsroot.txt
#                      Example to configure only the first interface: "ip=eth0:dhcp"
# QB_CMDLINE_IP_TAP: This parameter is similar to the QB_CMDLINE_IP_SLIRP parameter. Since the tap interface requires
#                    static IP configuration @CLIENT@ and @GATEWAY@ place holders are replaced by the IP and the gateway
#                    address of the qemu guest by runqemu.
#                    Example: "ip=192.168.7.@CLIENT@::192.168.7.@GATEWAY@:255.255.255.0::eth0"
#
# QB_ROOTFS_OPT: used as rootfs, e.g.,
#               "-drive id=disk0,file=@ROOTFS@,if=none,format=raw -device virtio-blk-device,drive=disk0"
#              Note, runqemu will replace "@ROOTFS@" with the one which is used, such as core-image-minimal-qemuarm64.ext4.
#
# QB_SERIAL_OPT: serial port, e.g., "-serial mon:stdio"
#
# QB_TCPSERIAL_OPT: tcp serial port option, e.g.,
#                   " -device virtio-serial-device -chardev socket,id=virtcon,port=@PORT@,host=127.0.0.1 -device virtconsole,chardev=virtcon"
#                   Note, runqemu will replace "@PORT@" with the port number which is used.
#
# QB_ROOTFS_EXTRA_OPT: extra options to be appended to the rootfs device in case there is none specified by QB_ROOTFS_OPT.
#                      Can be used to automatically determine the image from the other variables
#                      but define things link 'bootindex' when booting from EFI or 'readonly' when using squashfs
#                      without the need to specify a dedicated qemu configuration
# Usage:
# IMAGE_CLASSES += "qemuboot"
# See "runqemu help" for more info

QB_MEM ?= "-m 256"
QB_SERIAL_OPT ?= "-serial mon:stdio -serial null"
QB_DEFAULT_KERNEL ?= "${KERNEL_IMAGETYPE}"
QB_DEFAULT_FSTYPE ?= "ext4"
QB_OPT_APPEND ?= "-show-cursor"
QB_NETWORK_DEVICE ?= "-device virtio-net-pci,netdev=net0,mac=@MAC@"
QB_CMDLINE_IP_SLIRP ?= "ip=dhcp"
QB_CMDLINE_IP_TAP ?= "ip=192.168.7.@CLIENT@::192.168.7.@GATEWAY@:255.255.255.0"
QB_ROOTFS_EXTRA_OPT ?= ""

# This should be kept align with ROOT_VM
QB_DRIVE_TYPE ?= "/dev/sd"

# Create qemuboot.conf
addtask do_write_qemuboot_conf after do_rootfs before do_image
IMGDEPLOYDIR ?= "${WORKDIR}/deploy-${PN}-image-complete"

def qemuboot_vars(d):
    build_vars = ['MACHINE', 'TUNE_ARCH', 'DEPLOY_DIR_IMAGE',
                'KERNEL_IMAGETYPE', 'IMAGE_NAME', 'IMAGE_LINK_NAME',
                'STAGING_DIR_NATIVE', 'STAGING_BINDIR_NATIVE',
                'STAGING_DIR_HOST']
    return build_vars + [k for k in d.keys() if k.startswith('QB_')]

do_write_qemuboot_conf[vardeps] += "${@' '.join(qemuboot_vars(d))}"
do_write_qemuboot_conf[vardepsexclude] += "TOPDIR"
python do_write_qemuboot_conf() {
    import configparser

    qemuboot = "%s/%s.qemuboot.conf" % (d.getVar('IMGDEPLOYDIR'), d.getVar('IMAGE_NAME'))
    qemuboot_link = "%s/%s.qemuboot.conf" % (d.getVar('IMGDEPLOYDIR'), d.getVar('IMAGE_LINK_NAME'))
    finalpath = d.getVar("DEPLOY_DIR_IMAGE")
    topdir = d.getVar('TOPDIR')
    cf = configparser.ConfigParser()
    cf.add_section('config_bsp')
    for k in sorted(qemuboot_vars(d)):
        # qemu-helper-native sysroot is not removed by rm_work and
        # contains all tools required by runqemu
        if k == 'STAGING_BINDIR_NATIVE':
            val = os.path.join(d.getVar('BASE_WORKDIR'), d.getVar('BUILD_SYS'),
                               'qemu-helper-native/1.0-r1/recipe-sysroot-native/usr/bin/')
        else:
            val = d.getVar(k)
        # we only want to write out relative paths so that we can relocate images
        # and still run them
        if val.startswith(topdir):
            val = os.path.relpath(val, finalpath)
        cf.set('config_bsp', k, '%s' % val)

    # QB_DEFAULT_KERNEL's value of KERNEL_IMAGETYPE is the name of a symlink
    # to the kernel file, which hinders relocatability of the qb conf.
    # Read the link and replace it with the full filename of the target.
    kernel_link = os.path.join(d.getVar('DEPLOY_DIR_IMAGE'), d.getVar('QB_DEFAULT_KERNEL'))
    kernel = os.path.realpath(kernel_link)
    # we only want to write out relative paths so that we can relocate images
    # and still run them
    kernel = os.path.relpath(kernel, finalpath)
    cf.set('config_bsp', 'QB_DEFAULT_KERNEL', kernel)

    bb.utils.mkdirhier(os.path.dirname(qemuboot))
    with open(qemuboot, 'w') as f:
        cf.write(f)

    if qemuboot_link != qemuboot:
        if os.path.lexists(qemuboot_link):
           os.remove(qemuboot_link)
        os.symlink(os.path.basename(qemuboot), qemuboot_link)
}
