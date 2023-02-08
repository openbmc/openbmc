inherit phosphor-dbus-yaml

LOGGING_YAML_SUBDIRS ??= "${OBMC_ORG_YAML_SUBDIRS}"

do_install:append() {
    for yaml_d in ${LOGGING_YAML_SUBDIRS} ;
    do
        if [ -d ${S}/${yaml_d} ];
        then
            yaml_base=${S}
        elif [ -d ${S}/yaml/${yaml_d} ];
        then
            yaml_base=${S}/yaml
        else
            continue
        fi


        for yaml_f in $(find ${yaml_base}/${yaml_d} -name "*.errors.yaml" -or \
            -name "*.metadata.yaml") ;
        do
            subpath=$(realpath --relative-to=${yaml_base} ${yaml_f})
            install -d $(dirname ${D}${yaml_dir}/$subpath)

            install -m 0644 ${yaml_f} ${D}${yaml_dir}/$subpath
        done
    done
}
