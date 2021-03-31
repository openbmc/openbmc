SUMMARY = "Real-time file compressor"
DESCRIPTION = "lzop is a compression utility which is designed to be a companion to gzip. \n\
It is based on the LZO data compression library and its main advantages over \n\
gzip are much higher compression and decompression speed at the cost of some \n\
compression ratio. The lzop compression utility was designed with the goals \n\
of reliability, speed, portability and with reasonable drop-in compatibility \n\
to gzip."
HOMEPAGE = "http://www.lzop.org/"
DEPENDS += "lzo"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/lzop.c;beginline=5;endline=21;md5=23d767de7754eb24b9e900b025cf7fc8"

SRC_URI = "http://www.lzop.org/download/${BP}.tar.gz \
           file://acinclude.m4 \
          "
SRC_URI[md5sum] = "271eb10fde77a0a96b9cbf745e719ddf"
SRC_URI[sha256sum] = "7e72b62a8a60aff5200a047eea0773a8fb205caf7acbe1774d95147f305a2f41"

inherit autotools

do_configure_prepend () {
    install -Dm 0644 ${WORKDIR}/acinclude.m4 ${S}/acinclude.m4
}

BBCLASSEXTEND = "native nativesdk"
