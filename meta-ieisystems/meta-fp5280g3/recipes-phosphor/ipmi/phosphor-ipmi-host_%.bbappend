DEPENDS:append = " fp5280g3-yaml-config"

EXTRA_OEMESON:append = " \
        -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/fp5280g3-yaml-config/ipmi-sensors.yaml \
        -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/fp5280g3-yaml-config/ipmi-inventory-sensors.yaml \
        -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/fp5280g3-yaml-config/ipmi-fru-read.yaml \
        -Dget-dbus-active-software=enabled \
        -Dfw-ver-regex="([\\\\d]+).([\\\\d]+).([\\\\d]+)-dev-([\\\\d]+)-g([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})" \
        -Dmatches-map="1,2,5,6,7,8" \
        "

