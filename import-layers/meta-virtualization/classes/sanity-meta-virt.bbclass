addhandler virt_bbappend_distrocheck
virt_bbappend_distrocheck[eventmask] = "bb.event.SanityCheck"
python virt_bbappend_distrocheck() {
    skip_check = e.data.getVar('SKIP_META_VIRT_SANITY_CHECK') == "1"
    if 'virtualization' not in e.data.getVar('DISTRO_FEATURES').split() and not skip_check:
        bb.warn("You have included the meta-virtualization layer, but \
'virtualization' has not been enabled in your DISTRO_FEATURES. Some bbappend files \
may not take effect. See the meta-virtualization README for details on enabling \
virtualization support.")
}
