require docbook-sgml-dtd-native.inc

LICENSE = "OASIS"
LIC_FILES_CHKSUM = "file://LICENSE-OASIS;md5=c608985dd5f7f215e669e7639a0b1d2e"

DTD_VERSION = "3.1"

PR = "${INC_PR}.0"

# Note: the upstream sources are not distributed with a license file.
# LICENSE-OASIS is included as a "patch" to workaround this. When
# upgrading this recipe, please verify whether this is still needed.
SRC_URI = "http://www.docbook.org/sgml/3.1/docbk31.zip \
           file://LICENSE-OASIS"

SRC_URI[md5sum] = "432749c0c806dbae81c8bcb70da3b5d3"
SRC_URI[sha256sum] = "20261d2771b9a052abfa3d8fab1aa62be05791a010281c566f9073bf0e644538"

do_compile() {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/sgml-dtd-3.html
	# for details.
	sed -i -e '/ISO 8879/d' -e 's|DTDDECL "-//OASIS//DTD DocBook V3.1//EN"|SGMLDECL|g' docbook.cat
}
