PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'tpm2', '', d)}"

# for encrypted filesystems
PACKAGECONFIG:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'cryptsetup cryptsetup-plugins efi openssl repart', '', d)} \
"

# ukify.py and systemd-measure don't work in cross compile environment without
# a tpm2 device, thus switch from measured-uki (new in v256) back to tpm2 
# (default before v256).
# TODO: use swtpm-native to calculate TPM measurements
do_install:append() {
    if "${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'true', 'false', d)}"; then
        sed -i -e "s/^ConditionSecurity=measured-uki/ConditionSecurity=tpm2/g" \
            $( grep -rl ^ConditionSecurity=measured-uki ${D} )
    fi
}
