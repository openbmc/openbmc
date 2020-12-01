BBCLASSEXTEND = "nativesdk"

require qemu.inc

# error: a parameter list without types is only allowed in a function definition
#            void (*_function)(sigval_t);
COMPATIBLE_HOST_libc-musl = 'null'

DEPENDS = "glib-2.0 zlib pixman bison-native"

RDEPENDS_${PN}_class-target += "bash"

# Does not compile for -Og because that level does not clean up dead-code.
# See lockable.h.
#
DEBUG_BUILD = "0"

EXTRA_OECONF_append_class-target = " --target-list=${@get_qemu_target_list(d)}"
EXTRA_OECONF_append_class-target_mipsarcho32 = "${@bb.utils.contains('BBEXTENDCURR', 'multilib', ' --disable-capstone', '', d)}"
EXTRA_OECONF_append_class-nativesdk = " --target-list=${@get_qemu_target_list(d)}"

do_install_append_class-nativesdk() {
     ${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}

PACKAGECONFIG ??= " \
    fdt sdl kvm \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa xen', d)} \
"
PACKAGECONFIG_class-nativesdk ??= "fdt sdl kvm"
