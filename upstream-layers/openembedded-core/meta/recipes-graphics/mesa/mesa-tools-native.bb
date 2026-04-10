require mesa.inc
inherit_defer native

SUMMARY += " (tools only)"

PACKAGECONFIG = "tools asahi imagination panfrost"
# llvm required for libclc
PACKAGECONFIG += "gallium-llvm"
# Doesn't compile without wayland-scanner if PLATFORMS has wayland in, and,
# doesn't compile at all if PLATFORMS is empty so add x11 and wayland
# to PACKAGECONFIG like in mesa.inc
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"

DEPENDS += "libclc-native spirv-tools-native spirv-llvm-translator-native"

EXTRA_OEMESON += " \
    -Dmesa-clc=enabled -Dinstall-mesa-clc=true -Dmesa-clc-bundle-headers=enabled \
    -Dprecomp-compiler=enabled -Dinstall-precomp-compiler=true \
"
