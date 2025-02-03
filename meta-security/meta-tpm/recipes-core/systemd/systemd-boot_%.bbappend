DEPENDS += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'tpm2-tss libtss2 libtss2-tcti-device', '', d)} \
"

EXTRA_OEMESON:append= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', '-Dtpm2=true', '', d)} \
"
