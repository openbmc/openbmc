SUMMARY = "Encoding files for Poppler"
DESCRIPTION = "Encoding files for use with poppler that enable poppler to \
               correctly render CJK and Cyrrilic."
LICENSE = "BSD & GPLv2 & GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4870b98343f0bbb25fa43b9d2ba59448 \
                    file://COPYING.adobe;md5=63c6a8a9df204c00461fa5f163d8a663 \
                    file://COPYING.gpl2;md5=751419260aa954499f7abaabaa882bbe \
"

inherit allarch

INHIBIT_DEFAULT_DEPS = "1"

CMAP_RESOURCES_BASE = "https://github.com/adobe-type-tools/cmap-resources/raw/0561ebca035813ed04c3485bca636a0aa7abdc1d/cmapresources_identity-0/CMap"

SRC_URI = "http://poppler.freedesktop.org/${BP}.tar.gz \
           ${CMAP_RESOURCES_BASE}/Identity-H;name=idh \
           ${CMAP_RESOURCES_BASE}/Identity-V;name=idv"

SRC_URI[md5sum] = "00f8989c804de84af0ba2ea629949980"
SRC_URI[sha256sum] = "1096a18161f263cccdc6d8a2eb5548c41ff8fcf9a3609243f1b6296abdf72872"
SRC_URI[idh.md5sum] = "009c93cf0141ab7bd6acb7eea14306cc"
SRC_URI[idh.sha256sum] = "ae702c203a82ea124e9b96590f821db6fbf8754e2c4547a9dba0e82f94739e95"
SRC_URI[idv.md5sum] = "2f32a45d43d001c26eeac6b878855fbf"
SRC_URI[idv.sha256sum] = "89a85daf7031e93c883e76b9168a226dfd585bf5506e9e1956772163f15cb082"

do_compile() {
}

do_install() {
    oe_runmake install DESTDIR=${D} prefix=${prefix} datadir=${datadir}
    install -d ${D}${datadir}/poppler/cMap
    install -m644 ${WORKDIR}/Identity-* ${D}${datadir}/poppler/cMap/
}

FILES_${PN} += "${datadir}"
