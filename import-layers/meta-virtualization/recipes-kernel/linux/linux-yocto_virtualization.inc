FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto:"

SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
	    file://vswitch.scc \
	    file://lxc.scc \
            file://docker.scc \
	    "
KERNEL_FEATURES_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'features/kvm/qemu-kvm-enable.scc', '', d)}"

KERNEL_MODULE_AUTOLOAD += "openvswitch"
KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'kvm', '', d)}"
KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'kvm-amd', '', d)}"
KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'kvm-intel', '', d)}"

# aufs kernel support required for xen-image-minimal
KERNEL_FEATURES_append += "${@bb.utils.contains('DISTRO_FEATURES', 'aufs', ' features/aufs/aufs-enable.scc', '', d)}"

# xen kernel support
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' file://xen.scc', '', d)}"
