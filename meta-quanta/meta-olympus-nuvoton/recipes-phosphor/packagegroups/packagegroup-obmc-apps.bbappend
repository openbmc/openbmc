inherit entity-utils
RDEPENDS:${PN}-fan-control:remove = " \
       phosphor-fan-control \
       phosphor-fan-monitor \
       "
RDEPENDS:${PN}-inventory:remove = " \
       phosphor-fan-presence-tach \
       "

RDEPENDS:${PN}-fru-ipmi:remove = " \
       ${@entity_enabled(d, '','fru-device')} \
       "
