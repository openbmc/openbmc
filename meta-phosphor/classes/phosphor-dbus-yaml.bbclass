yaml_dir = "${datadir}/phosphor-dbus-yaml/yaml"

PACKAGE_BEFORE_PN += "${PN}-yaml"
FILES_${PN}-yaml += "${yaml_dir}"
