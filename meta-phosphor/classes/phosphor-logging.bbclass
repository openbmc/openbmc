callouts_datadir="${datadir}/phosphor-logging/callouts"
yaml_dir = "${datadir}/sdbus/yaml"

PACKAGE_BEFORE_PN += "${PN}-yaml"
FILES_${PN}-yaml += "${yaml_dir}"
