DESCRIPTION = "wireshark - a popular network protocol analyzer"
HOMEPAGE = "http://www.wireshark.org"
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e271234ba1a13c6e512e76b94ac2f77"

DEPENDS = "pcre expat glib-2.0 glib-2.0-native libgcrypt libgpg-error libxml2 bison-native"

DEPENDS_append_class-target = " wireshark-native chrpath-replacement-native "

SRC_URI = "https://1.as.dl.wireshark.org/src/all-versions/${BP}.tar.xz \
           file://0001-Add-libm-to-link-for-fmod-API.patch \
          "

UPSTREAM_CHECK_URI = "https://1.as.dl.wireshark.org/src"

SRC_URI[md5sum] = "b30ba5b9b48ed2ff91c0ce357d33b46b"
SRC_URI[sha256sum] = "487933ea075bdbb25d8df06017d9c4f49fc20eb7f6ec80af086718ed5550e863"

PE = "1"

inherit cmake pkgconfig pythonnative perlnative upstream-version-is-even

ARM_INSTRUCTION_SET = "arm"

# Options: gtk+, gtk+3 and qt5
GTK = "gtk+3"

PACKAGECONFIG ?= "libpcap gnutls libnl libcap sbc"
PACKAGECONFIG_append_class-target = " ${@bb.utils.contains("DISTRO_FEATURES", "x11", " ${GTK}", "", d)}"

PACKAGECONFIG_class-native = "libpcap gnutls ssl libssh"

PACKAGECONFIG[libcap] = "-DENABLE_CAP=ON,-DENABLE_CAP=OFF -DENABLE_PCAP_NG_DEFAULT=ON, libcap"
PACKAGECONFIG[libpcap] = "-DENABLE_PCAP=ON,-DENABLE_PCAP=OFF -DENABLE_PCAP_NG_DEFAULT=ON , libpcap"
PACKAGECONFIG[libsmi] = "-DENABLE_SMI=ON,-DENABLE_SMI=OFF,libsmi"
PACKAGECONFIG[libnl] = ",,libnl"
PACKAGECONFIG[portaudio] = "-DENABLE_PORTAUDIO=ON,-DENABLE_PORTAUDIO=OFF, portaudio-v19"
PACKAGECONFIG[gtk+] = "-DENABLE_GTK=ON -DBUILD_wireshark_gtk=ON, -DENABLE_GTK=OFF, gtk+"
PACKAGECONFIG[gtk+3] = "-DENABLE_GTK3=ON -DBUILD_wireshark_gtk=ON, -DENABLE_GTK3=OFF, gtk+3"
PACKAGECONFIG[gnutls] = "-DENABLE_GNUTLS=ON,-DENABLE_GNUTLS=OFF, gnutls"
PACKAGECONFIG[ssl] = ",,openssl"
PACKAGECONFIG[krb5] = "-DENABLE_KRB5=ON,-DENABLE_KRB5=OFF, krb5"
PACKAGECONFIG[lua] = "-DENABLE_LUA=ON,-DENABLE_LUA=OFF, lua"
PACKAGECONFIG[zlib] = "-DENABLE_ZLIB=ON,-DENABLE_ZLIB=OFF, zlib"
PACKAGECONFIG[geoip] = ",, geoip"
PACKAGECONFIG[plugins] = "-DENABLE_PLUGINS=ON,-DENABLE_PLUGINS=OFF"
PACKAGECONFIG[sbc] = "-DENABLE_SBC=ON,-DENABLE_SBC=OFF, sbc"
PACKAGECONFIG[libssh] = ",,libssh2"
PACKAGECONFIG[lz4] = "-DENABLE_LZ4=ON,-DENABLE_LZ4=OFF, lz4"

# these next two options require addional layers
PACKAGECONFIG[c-ares] = "-DENABLE_CARES=ON,-DENABLE_CARES=OFF, c-ares"
PACKAGECONFIG[qt5] = "-DENABLE_QT5=ON -DBUILD_wireshark=ON, -DENABLE_QT5=OFF -DBUILD_wireshark=OFF, qtbase"

EXTRA_OECMAKE += "-DENABLE_NETLINK=ON \
                  -DBUILD_mmdbresolve=OFF \
                  -DBUILD_randpktdump=OFF \
                  -DBUILD_androiddump=OFF \
                  -DBUILD_dcerpcidl2wrs=OFF \
                  -DM_INCLUDE_DIR=${includedir} \
                  -DM_LIBRARY=${libdir} \
                 "
CFLAGS_append = " -lm"

do_install_append_class-native() {
	install -d ${D}${bindir}
	for f in lemon
	do
		install -m 0755 ${B}/run/$f ${D}${bindir}
	done
}

do_install_append_class-target() {
	for f in `find ${D}${libdir} ${D}${bindir} -type f -executable`
	do
		chrpath --delete $f
	done
}

PACKAGE_BEFORE_PN += "tshark"
PACKAGE_BEFORE_PN += "${PN}-gtk"

FILES_tshark = "${bindir}/tshark ${mandir}/man1/tshark.*"
FILES_${PN}-gtk = "${bindir}/wireshark-gtk ${datadir}/icons ${datadir}/appdata \
                   ${datadir}/applications ${datadir}/wireshark ${datadir}/mime \
                   ${mandir}/man1/wireshark.* "

FILES_${PN} += "${datadir}*"

RDEPENDS_tshark = "wireshark"
RDEPENDS_${PN}-gtk = "${@bb.utils.contains("DISTRO_FEATURES", "x11", "${GTK}", "", d)} wireshark"

BBCLASSEXTEND = "native"
