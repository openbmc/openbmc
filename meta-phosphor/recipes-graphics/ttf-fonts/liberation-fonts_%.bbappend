do_install () {
        install -d ${D}${datadir}/fonts/ttf/
        for i in LiberationMono-Regular.ttf; do
                install -m 0644 $i ${D}${prefix}/share/fonts/ttf/${i}
        done
        install -d ${D}${sysconfdir}/fonts/conf.d/
        install -m 0644 ${UNPACKDIR}/30-liberation-aliases.conf ${D}${sysconfdir}/fonts/conf.d/
}
