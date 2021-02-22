# General config settings.
EXTRA_OECONF_append_class-target = " shared no-hw no-err no-psk no-srp "

# Disable SSL (keep TLS only).
EXTRA_OECONF_append_class-target = " no-ssl2 no-ssl3 "

# Disable various algorithms.
EXTRA_OECONF_append_class-target = " no-md4 no-rmd160 no-whirlpool  \
        no-rc2 no-rc4 no-bf no-cast no-gost "
do_configure_append() {
    oe_runmake depend
}

# We don't want to depend on perl in our image
RDEPENDS_${PN}-bin_remove = "perl"
FILES_${PN}-misc_append = " ${bindir}/c_rehash"
