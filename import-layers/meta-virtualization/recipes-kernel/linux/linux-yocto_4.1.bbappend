FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
	    file://vswitch.scc \
	    file://lxc.scc \
	    "
KERNEL_FEATURES_append = " features/kvm/qemu-kvm-enable.scc"

KERNEL_MODULE_AUTOLOAD += "openvswitch"
KERNEL_MODULE_AUTOLOAD += "kvm"
KERNEL_MODULE_AUTOLOAD += "kvm-amd"
KERNEL_MODULE_AUTOLOAD += "kvm-intel"

# aufs kernel support required for xen-image-minimal
KERNEL_FEATURES_append += "${@base_contains('DISTRO_FEATURES', 'aufs', ' features/aufs/aufs-enable.scc', '', d)}"

# xen kernel support
SRC_URI += "${@base_contains('DISTRO_FEATURES', 'xen', ' file://xen.scc', '', d)}"
