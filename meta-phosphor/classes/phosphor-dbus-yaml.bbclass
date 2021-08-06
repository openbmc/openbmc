yaml_dir = "${datadir}/phosphor-dbus-yaml/yaml"

PACKAGE_BEFORE_PN += "${PN}-yaml"
FILES:${PN}-yaml += "${yaml_dir}"
