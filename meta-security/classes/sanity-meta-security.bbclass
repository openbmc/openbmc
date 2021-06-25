addhandler security_bbappend_distrocheck
security_bbappend_distrocheck[eventmask] = "bb.event.SanityCheck"
python security_bbappend_distrocheck() {
    skip_check = e.data.getVar('SKIP_META_SECUIRTY_SANITY_CHECK') == "1"
    if 'security' not in e.data.getVar('DISTRO_FEATURES').split() and not skip_check:
        bb.warn("You have included the meta-security layer, but \
'security' has not been enabled in your DISTRO_FEATURES. Some bbappend files \
and preferred version setting may not take effect. See the meta-security README \
for details on enabling security support.")
}
