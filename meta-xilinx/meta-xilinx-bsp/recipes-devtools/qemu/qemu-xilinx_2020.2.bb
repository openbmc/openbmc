require qemu.inc
require qemu-xilinx.inc

BBCLASSEXTEND = "nativesdk"

RDEPENDS_${PN}_class-target += "bash"

PROVIDES_class-nativesdk = "nativesdk-qemu"
RPROVIDES_${PN}_class-nativesdk = "nativesdk-qemu"

EXTRA_OECONF_append_class-target = " --target-list=${@get_qemu_target_list(d)}"
EXTRA_OECONF_append_class-nativesdk = " --target-list=${@get_qemu_target_list(d)}"

do_install_append_class-nativesdk() {
    ${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}
