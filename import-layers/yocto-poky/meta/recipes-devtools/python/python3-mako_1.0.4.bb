require python-mako.inc

inherit setuptools3

RDEPENDS_${PN} = "python3-threading \
                  python3-netclient \
                  python3-html \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
