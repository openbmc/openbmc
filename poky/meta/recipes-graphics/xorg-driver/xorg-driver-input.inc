require xorg-driver-common.inc

DEPENDS += "xorgproto"

python add_xorg_abi_depends() {
    _add_xorg_abi_depends(d, "input")
}
PACKAGEFUNCS =+ "add_xorg_abi_depends"

FILES_${PN} += " ${libdir}/xorg/modules/input/*.so \
                 ${datadir}/X11/xorg.conf.d \
                 "
