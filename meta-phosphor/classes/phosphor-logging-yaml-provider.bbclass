inherit phosphor-dbus-yaml

LOGGING_YAML_SUBDIRS ??= "${OBMC_ORG_YAML_SUBDIRS}"

do_install_append() {
    for yaml_d in ${LOGGING_YAML_SUBDIRS} ;
    do
        if [ ! -d ${S}/${yaml_d} ];
        then
            continue
        fi

        for yaml_f in $(find ${S}/${yaml_d} -name "*.errors.yaml" -or \
            -name "*.metadata.yaml") ;
        do
            subpath=$(realpath --relative-to=${S} ${yaml_f})
            install -d $(dirname ${D}${yaml_dir}/$subpath)

            install -m 0644 ${yaml_f} ${D}${yaml_dir}/$subpath
        done
    done
}
