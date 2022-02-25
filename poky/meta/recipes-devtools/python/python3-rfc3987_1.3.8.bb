SUMMARY = "Parsing and validation of URIs (RFC 3986) and IRIs (RFC 3987)"
HOMEPAGE = "https://pypi.org/project/rfc3987/"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=9;md5=2b723edf67b2f3088bc5e339b1ceda2d"

SRC_URI[md5sum] = "b6c4028acdc788a9ba697e1c1d6b896c"
SRC_URI[sha256sum] = "d3c4d257a560d544e9826b38bc81db676890c79ab9d7ac92b39c7a253d5ca733"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
