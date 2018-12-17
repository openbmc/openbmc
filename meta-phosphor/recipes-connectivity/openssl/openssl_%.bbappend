# General config settings.
EXTRA_OECONF_append_class-target = " shared no-hw no-err no-psk no-srp "

# Disable SSL (keep TLS only).
EXTRA_OECONF_append_class-target = " no-ssl2 no-ssl3 "

# Disable various algorithms.
EXTRA_OECONF_append_class-target = " no-idea no-md2 no-mdc2 no-rc5 no-md4 \
        no-rmd160 no-whirlpool no-camellia \
        no-rc2 no-rc4 no-bf no-cast no-seed no-gost "

do_configure_append() {
    oe_runmake depend
}
