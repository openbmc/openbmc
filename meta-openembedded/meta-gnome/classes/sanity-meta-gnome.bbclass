addhandler gnome_bbappend_distrocheck
gnome_bbappend_distrocheck[eventmask] = "bb.event.SanityCheck"
python gnome_bbappend_distrocheck() {
    skip_check = e.data.getVar('SKIP_META_GNOME_SANITY_CHECK') == "1"
    if 'x11' not in e.data.getVar('DISTRO_FEATURES').split() and not skip_check:
        bb.warn("You have included the meta-gnome layer, but \
'x11' has not been enabled in your DISTRO_FEATURES. Some bbappend files \
may not take effect. See the meta-gnome README for details on enabling \
meta-gnome support.")
}
