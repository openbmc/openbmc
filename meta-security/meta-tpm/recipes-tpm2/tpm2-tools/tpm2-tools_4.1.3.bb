SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0eb1216e46938bd723098d93a23c3bcc"
SECTION = "tpm"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI += "file://0001-tpm2_import-fix-fixed-AES-key-CVE-2021-3565.patch"

SRC_URI[md5sum] = "48e0f58232b6a86fe4d007acf12af283"
SRC_URI[sha256sum] = "bb5d3310620e75468fe33dbd530bd73dd648c70ec707b4579c74d9f63fc82704"
SRC_URI[sha1sum] = "b2cef4d06817a6859082d50863464a858a493a63"
SRC_URI[sha384sum] = "996c33201c92bcbdbf8f11f84d25a8e2938c330fb7fb66a47eafb3c5a41fab9bcb9a769dc20226accdea2486b626bd68"
SRC_URI[sha512sum] = "bf1ba9f8a4e12c71987650b309710574cc796e78d26c5de1cae77b0e150cea0f3b3695e56415be1994c4a6ad90e8f991d5db603138933fd21c46f7b86148a9b4"

inherit autotools pkgconfig bash-completion
