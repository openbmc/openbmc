SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.mit;md5=ca574f2891cf528b3e7a2ee570337e7c"

SRC_URI[md5sum] = "903c518c8829de4ee255cff68ff8da45"
SRC_URI[sha256sum] = "eb6545dbeb1aa69ab1fb4809bfbf5a8705e44d92ef8fc7c2361682a47c46c778"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
