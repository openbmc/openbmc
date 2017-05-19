inherit openpower-dbus-interfaces

EXTRA_OECONF_append = " \
       OP_YAML_DIR=${STAGING_DIR_NATIVE}${op_yaml_dir} \
       "
