callouts_datadir="${datadir}/phosphor-logging/callouts"
error_yaml_dir = "${datadir}/phosphor-logging/error/yaml"

PACKAGE_BEFORE_PN += "${PN}-yaml"
FILES_${PN}-yaml = "${datadir}/${PN}/yaml"
FILES_${PN}-yaml += "${datadir}/phosphor-logging/error/yaml"
