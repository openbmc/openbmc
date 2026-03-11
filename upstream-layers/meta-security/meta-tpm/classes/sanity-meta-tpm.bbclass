addhandler tpm_machinecheck
tpm_machinecheck[eventmask] = "bb.event.SanityCheck"
python tpm_machinecheck() {
    skip_check = e.data.getVar('SKIP_META_TPM_SANITY_CHECK') == "1"
    if 'tpm' not in e.data.getVar('DISTRO_FEATURES').split() and \
       'tpm2' not in e.data.getVar('DISTRO_FEATURES').split() and \
       not skip_check:
        bb.warn("You have included the meta-tpm layer, but \
'tpm or tpm2' has not been enabled in your DISTRO_FEATURES. Some bbappend files \
and preferred version setting may not take effect. See the meta-tpm README \
for details on enabling tpm support.")
}
