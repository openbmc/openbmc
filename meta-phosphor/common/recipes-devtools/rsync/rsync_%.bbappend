EXTRA_OECONF_append += " --disable-largefile --disable-locale --disable-iconv \
                         --without-included-popt --without-included-zlib"

PACKAGECONFIG = ""

DEPENDS_append = " popt zlib"
