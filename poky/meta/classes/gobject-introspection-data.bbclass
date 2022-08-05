# This variable is set to True if gobject-introspection-data is in
# DISTRO_FEATURES and qemu-usermode is in MACHINE_FEATURES, and False otherwise.
#
# It should be used in recipes to determine whether introspection data should be built,
# so that qemu use can be avoided when necessary.
GI_DATA_ENABLED ?= "${@bb.utils.contains('DISTRO_FEATURES', 'gobject-introspection-data', \
                      bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d), 'False', d)}"

do_compile:prepend() {
    # This prevents g-ir-scanner from writing cache data to $HOME
    export GI_SCANNER_DISABLE_CACHE=1
}
