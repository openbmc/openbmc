# General config settings.
EXTRA_OECONF:append:class-target = " shared no-hw no-err no-psk no-srp "
# Disable SSL (keep TLS only).
EXTRA_OECONF:append:class-target = " no-ssl2 no-ssl3 "
# Disable various algorithms.
EXTRA_OECONF:append:class-target = " \
    no-rmd160 \
    no-whirlpool \
    no-rc2 \
    no-rc4 \
    no-bf \
    no-cast \
    no-gost \
"

do_configure:append() {
    oe_runmake depend
}

# We don't want to depend on perl in our image
RDEPENDS:${PN}-bin:remove = "perl"

FILES:${PN}-misc:append = " ${bindir}/c_rehash"
