DESCRIPTION ?= "TrueType font package ${PN}"
SECTION = "fonts"

# we don't need a compiler nor a c library for these fonts
INHIBIT_DEFAULT_DEPS = "1"

do_install() {
    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.tt[cf]' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

inherit allarch fontcache
