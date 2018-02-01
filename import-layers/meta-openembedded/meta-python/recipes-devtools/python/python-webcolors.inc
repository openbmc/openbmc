SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=358df7e9eb65e1843dbb36abd476c5c0"

SRC_URI[md5sum] = "4733fa1077f680bbdd918cdef1e32c11"
SRC_URI[sha256sum] = "e47e68644d41c0b1f1e4d939cfe4039bdf1ab31234df63c7a4f59d4766487206"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-stringold \
"

BBCLASSEXTEND = "native nativesdk"
