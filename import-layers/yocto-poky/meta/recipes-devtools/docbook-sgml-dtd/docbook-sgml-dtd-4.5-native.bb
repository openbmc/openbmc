require docbook-sgml-dtd-native.inc

LICENSE = "OASIS"
LIC_FILES_CHKSUM = "file://LICENSE-OASIS;md5=c608985dd5f7f215e669e7639a0b1d2e"

DTD_VERSION = "4.5"

PR = "${INC_PR}.0"
PV = "4.5"

SRC_URI[md5sum] = "07c581f4bbcba6d3aac85360a19f95f7"
SRC_URI[sha256sum] = "8043e514e80c6c19cb146b5d37937d1305bf3abf9b0097c36df7f70f611cdf43"

do_compile() {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/sgml-dtd.html
	# for details.
	sed -i -e '/ISO 8879/d' -e'/gml/d' docbook.cat
}
