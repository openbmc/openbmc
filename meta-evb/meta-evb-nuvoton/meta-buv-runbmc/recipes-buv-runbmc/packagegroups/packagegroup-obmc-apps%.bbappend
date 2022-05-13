inherit buv-entity-utils

RDEPENDS:${PN}-fru-ipmi:remove:buv-runbmc = "${@entity_enabled(d, '', 'fru-device')}"
RDEPENDS:${PN}-inventory:remove:buv-runbmc = " phosphor-fan-presence-tach"
