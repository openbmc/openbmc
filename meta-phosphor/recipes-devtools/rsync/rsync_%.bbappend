EXTRA_OECONF_append_class-target += " --disable-largefile --disable-locale \
                                      --disable-iconv --without-included-popt \
                                      --without-included-zlib"

PACKAGECONFIG = ""

DEPENDS_append_class-target = " popt zlib"
