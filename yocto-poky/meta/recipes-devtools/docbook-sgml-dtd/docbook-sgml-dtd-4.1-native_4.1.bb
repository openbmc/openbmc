require docbook-sgml-dtd-native.inc

LICENSE = "OASIS"
LIC_FILES_CHKSUM = "file://LICENSE-OASIS;md5=c608985dd5f7f215e669e7639a0b1d2e"

DTD_VERSION = "4.1"

PR = "${INC_PR}.0"

# Note: the upstream sources are not distributed with a license file.
# LICENSE-OASIS is included as a "patch" to workaround this. When
# upgrading this recipe, please verify whether this is still needed.
SRC_URI = "http://docbook.org/sgml/4.1/docbk41.zip \
           file://LICENSE-OASIS"

SRC_URI[md5sum] = "489f6ff2a2173eb1e14216c10533ede2"
SRC_URI[sha256sum] = "deaafcf0a3677692e7ad4412c0e41c1db3e9da6cdcdb3dd32b2cc1f9c97d6311"

do_compile() {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/sgml-dtd.html
	# for details.
	sed -i -e '/ISO 8879/d' -e '/gml/d' docbook.cat
}
