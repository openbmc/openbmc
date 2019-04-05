BBCLASSEXTEND = "nativesdk"

require qemu.inc

DEPENDS = "glib-2.0 zlib pixman"

RDEPENDS_${PN}_class-target += "bash"

EXTRA_OECONF_append_class-target = " --target-list=${@get_qemu_target_list(d)}"
EXTRA_OECONF_append_class-nativesdk = " --target-list=${@get_qemu_target_list(d)}"

do_install_append_class-nativesdk() {
     ${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}

PACKAGECONFIG ??= " \
    fdt sdl kvm \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa xen', d)} \
"
PACKAGECONFIG_class-nativesdk ??= "fdt sdl kvm"


