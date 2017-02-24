SUMMARY = "Real-time file compressor"
DESCRIPTION = "lzop is a compression utility which is designed to be a companion to gzip. \n\
It is based on the LZO data compression library and its main advantages over \n\
gzip are much higher compression and decompression speed at the cost of some \n\
compression ratio. The lzop compression utility was designed with the goals \n\
of reliability, speed, portability and with reasonable drop-in compatibility \n\
to gzip."
DEPENDS += "lzo"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dfeaf3dc4beef4f5a7bdbc35b197f39e \
                    file://src/lzop.c;beginline=5;endline=21;md5=6797bd3ed0a1a49327b7ebf9366ebd86"

SRC_URI = "http://www.lzop.org/download/${BP}.tar.gz \
           file://acinclude.m4 \
           file://x32_abi_miniacc_h.patch \
           file://0001-use-static-inlines-as-the-external-inline-definition.patch \
           file://lzop-1.03-gcc6.patch \
          "
SRC_URI[md5sum] = "006c5e27fb78cdd14a628fdfa5aa1905"
SRC_URI[sha256sum] = "c1425b8c77d49f5a679d5a126c90ea6ad99585a55e335a613cae59e909dbb2c9"

inherit autotools

do_configure_prepend () {
    install -Dm 0644 ${WORKDIR}/acinclude.m4 ${S}/acinclude.m4
}

BBCLASSEXTEND += "native nativesdk"
