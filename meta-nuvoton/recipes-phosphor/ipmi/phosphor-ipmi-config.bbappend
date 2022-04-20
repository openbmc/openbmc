ENTITY_MAPPING ?= "empty"

do_install:append:nuvoton() {
  # Set entity-map.json to empty json for Nuvoton BMC by default.
  # Each system can override it if needed.
  if [ "${ENTITY_MAPPING}" = "empty" ]; then
    echo "[]" > ${D}${datadir}/ipmi-providers/entity-map.json
  fi
}
