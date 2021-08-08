BBCLASSEXTEND = "nativesdk"

require qemu.inc

# error: a parameter list without types is only allowed in a function definition
#            void (*_function)(sigval_t);
COMPATIBLE_HOST:libc-musl = 'null'

DEPENDS = "glib-2.0 zlib pixman bison-native ninja-native meson-native"

RDEPENDS:${PN}:class-target += "bash"

EXTRA_OECONF:append:class-target = " --target-list=${@get_qemu_target_list(d)}"
EXTRA_OECONF:append:class-target:mipsarcho32 = "${@bb.utils.contains('BBEXTENDCURR', 'multilib', ' --disable-capstone', '', d)}"
EXTRA_OECONF:append:class-nativesdk = " --target-list=${@get_qemu_target_list(d)}"

do_install:append:class-nativesdk() {
     ${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}

PACKAGECONFIG ??= " \
    fdt sdl kvm pie \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa xen', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'virglrenderer glx', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'seccomp', d)} \
"
PACKAGECONFIG:class-nativesdk ??= "fdt sdl kvm pie \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'virglrenderer glx', '', d)} \
"
