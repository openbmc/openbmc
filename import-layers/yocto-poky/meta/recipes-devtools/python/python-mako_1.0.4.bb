require python-mako.inc

inherit setuptools

RDEPENDS_${PN} = "python-threading \
                  python-netclient \
                  python-html \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

# The same utility is packaged in python3-mako, so it would conflict
do_install_append() {
    rm -f ${D}${bindir}/mako-render
    rmdir ${D}${bindir}
}
