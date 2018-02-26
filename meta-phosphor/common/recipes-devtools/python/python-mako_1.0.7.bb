require recipes-devtools/python/python-mako.inc

inherit setuptools

RDEPENDS_${PN} = "python-threading \
                  python-netclient \
                  python-html \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
