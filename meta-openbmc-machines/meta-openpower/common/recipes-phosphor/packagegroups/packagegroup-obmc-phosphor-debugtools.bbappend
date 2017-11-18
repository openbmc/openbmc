RDEPENDS_${PN}_append = " ${@cf_enabled(d, 'obmc-openpower-extra', '\
    pflash \
    gard \
    fsidbg \
    ')}"
