EXTRA_OECONF:append:class-target = " \
    --disable-locale --disable-iconv \
    --without-included-popt --without-included-zlib \
"

PACKAGECONFIG = ""

DEPENDS:append:class-target = " popt zlib"
