# until fully tested, prefer `libwayland-egl` provided by `userland` instead of `wayland` when not using vc4graphics
do_install:append:rpi () {
    if [ "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "1", "0", d)}" = "0" ]; then
        rm -f ${D}${libdir}/libwayland-egl*
        rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
    fi
}
