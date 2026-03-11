addhandler integrity_bbappend_distrocheck
integrity_bbappend_distrocheck[eventmask] = "bb.event.SanityCheck"
python integrity_bbappend_distrocheck() {
    skip_check = e.data.getVar('SKIP_META_INTEGRITY_SANITY_CHECK') == "1"
    if 'integrity' not in e.data.getVar('DISTRO_FEATURES').split() and not skip_check:
        bb.warn("You have included the meta-integrity layer, but \
'integrity' has not been enabled in your DISTRO_FEATURES. Some bbappend files \
and preferred version setting may not take effect. See the meta-integrity README \
for details on enabling integrity support.")
}
