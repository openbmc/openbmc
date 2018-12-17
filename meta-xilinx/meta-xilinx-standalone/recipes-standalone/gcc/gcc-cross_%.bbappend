# By using tclibc-baremetal we loose sysroot functionality due to some
# append/override behavior We need to get that back , the following append
# overrides everything on EXTRA_OECONF for gcc cross target it avoids
# overlapping with crt0 because of --enable-linker-id from EXTRA_OECONF

EXTRA_OECONF_BASE_pn-gcc-cross-${TARGET_ARCH}_append = " \
    ${LTO} \
    ${SSP} \
    --enable-libitm \
    --disable-bootstrap \
    --disable-libmudflap \
    --with-system-zlib \
    --with-ppl=no \
    --with-cloog=no \
    --enable-checking=release \
    --enable-cheaders=c_global \
    --without-isl \
"
