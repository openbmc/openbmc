DESCRIPTION = "wireshark - a popular network protocol analyzer"
HOMEPAGE = "http://www.wireshark.org"
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e271234ba1a13c6e512e76b94ac2f77"

DEPENDS = "pcre expat glib-2.0 glib-2.0-native"

SRC_URI = "https://2.na.dl.wireshark.org/src/all-versions/${BP}.tar.bz2"

PE = "1"

SRC_URI[md5sum] = "ae3a1a43a6e3687f44a738fd15d78021"
SRC_URI[sha256sum] = "8574a5e1fdec7affae640924bd46c1aed1bd866e02632fa5625e1450e4a50707"

inherit autotools pkgconfig perlnative

ARM_INSTRUCTION_SET = "arm"

PACKAGECONFIG ?= "libpcap gnutls libnl libcap sbc"
PACKAGECONFIG += " ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gtk2 graphics", "", d)}"
#PACKAGECONFIG += " ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "gtk3", "", d)}"

PACKAGECONFIG[libcap] = "--with-libcap=${STAGING_LIBDIR}, --with-libcap=no --enable-pcap-ng-default , libcap"
PACKAGECONFIG[libpcap] = "--with-pcap=${STAGING_LIBDIR} --with-pcap-remote, --with-pcap=no --enable-pcap-ng-default  , libpcap"
PACKAGECONFIG[libsmi] = "--with-libsmi=yes, --with-libsmi=no, libsmi"
PACKAGECONFIG[libnl] = "--with-libnl=yes, --with-libnl=no, libnl"
PACKAGECONFIG[portaudio] = "--with-portaudio=yes, --with-portaudio=no, portaudio-v19"
PACKAGECONFIG[gtk2] = "--with-gtk=2, , gtk+"
PACKAGECONFIG[gtk3] = "--with-gtk=3, , gtk+3"
PACKAGECONFIG[graphics] = "--enable-wireshark, --with-gtk=no --disable-wireshark,"
PACKAGECONFIG[gnutls] = "--with-gnutls=yes, --with-gnutls=no, gnutls"
PACKAGECONFIG[gcrypt] = "--with-gcrypt=yes, --with-gcrypt=no, libgcrypt"
PACKAGECONFIG[ssl] = "--with-ssl=yes, --with-ssl=no, openssl"
PACKAGECONFIG[krb5] = "--with-krb5=yes, --with-krb5=no, krb5"
PACKAGECONFIG[lua] = "--with-lua=yes, --with-lua=no, lua"
PACKAGECONFIG[zlib] = "--with-zlib=yes, --with-zlib=no, zlib"
PACKAGECONFIG[geoip] = "--with-geoip=yes, --with-geoip=no, geoip"
PACKAGECONFIG[plugins] = "--with-plugins=yes, --with-plugins=no"
PACKAGECONFIG[sbc] = "--with-sbc=yes, --with-sbc=no, sbc"

PACKAGECONFIG[libssh] = "--with-ssh=yes, --with-ssh=no, libssh2"


# these next two options require addional layers
PACKAGECONFIG[c-ares] = "--with-c-ares=yes, --with-c-ares=no, c-ares"

EXTRA_OECONF += "--with-qt=no --enable-tshark --enable-rawshark"

# Currently wireshark does not install header files
do_install_append () {

	install -d ${D}/${includedir}/${BPN}
	install -d ${D}/${includedir}/${BPN}/epan
	install -d ${D}/${includedir}/${BPN}/epan/crypt
	install -d ${D}/${includedir}/${BPN}/epan/dfilter
	install -d ${D}/${includedir}/${BPN}/epan/dissectors
	install -d ${D}/${includedir}/${BPN}/epan/ftypes
	install -d ${D}/${includedir}/${BPN}/epan/wmem

	install config.h ${D}/${includedir}/${BPN}
	install ${S}/register.h ${D}/${includedir}/${BPN}
	install -D ${S}/epan/*.h ${D}/${includedir}/${BPN}/epan
	install -D ${S}/epan/crypt/*.h ${D}/${includedir}/${BPN}/epan/crypt
	install -D ${S}/epan/dfilter/*.h ${D}/${includedir}/${BPN}/epan/dfilter
	install -D ${S}/epan/dissectors/*.h ${D}/${includedir}/${BPN}/epan/dissectors
	install -D ${S}/epan/ftypes/*.h ${D}/${includedir}/${BPN}/epan/ftypes
	install -D ${S}/epan/wmem/*.h ${D}/${includedir}/${BPN}/epan/wmem
}

FILES_${PN} += "${datadir}*"
