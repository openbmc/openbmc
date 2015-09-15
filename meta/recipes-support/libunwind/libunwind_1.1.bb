require libunwind.inc

SRC_URI += "\
    file://Support-building-with-older-compilers.patch \
    file://AArch64-port.patch \
    file://Fix-test-case-link-failure-on-PowerPC-systems-with-Altivec.patch \
    file://Link-libunwind-to-libgcc_s-rather-than-libgcc.patch \
    file://0001-Invalid-dwarf-opcodes-can-cause-references-beyond-th.patch \
"

SRC_URI[md5sum] = "fb4ea2f6fbbe45bf032cd36e586883ce"
SRC_URI[sha256sum] = "9dfe0fcae2a866de9d3942c66995e4b460230446887dbdab302d41a8aee8d09a"
