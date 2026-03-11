require xorg-driver-common.inc

python add_xorg_abi_depends() {
    _add_xorg_abi_depends(d, "xinput")
}
PACKAGEFUNCS =+ "add_xorg_abi_depends"

FILES:${PN} += " ${libdir}/xorg/modules/input/*.so \
                 ${datadir}/X11/xorg.conf.d \
                 "
