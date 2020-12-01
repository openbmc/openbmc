SUMMARY = "Paho MQTT - C libraries for the MQTT and MQTT-SN protocols"
DESCRIPTION = "Client implementation of open and standard messaging protocols for Machine-to-Machine (M2M) and Internet of Things (IoT)."
HOMEPAGE = "http://www.eclipse.org/paho/"
SECTION = "console/network"
LICENSE = "EPL-2.0 | EDL-1.0"

LIC_FILES_CHKSUM = " \
    file://edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3 \
    file://epl-v20;md5=d9fc0efef5228704e7f5b37f27192723 \
    file://notice.html;md5=943f861069889acecebf51dfa24478e2 \
    file://about.html;md5=e5662cbb5f8fd5c9faac526e4077898e \
"

SRC_URI = "git://github.com/eclipse/paho.mqtt.c;protocol=http \
           file://0001-Fix-bug-of-free-with-musl.patch"

SRCREV = "3148fe2d5f4b87e16266dfe559c0764e16ca0546"

DEPENDS = "openssl"

S = "${WORKDIR}/git"

inherit cmake

do_configure_prepend() {
    sed -i s:\ lib/cmake:\ ${baselib}/cmake:g ${S}/src/CMakeLists.txt
}

do_install_append() {
    # paho-mqtt installes some thing that we don't want.
    rm -rf ${D}${prefix}/samples
    find ${D}${prefix} -maxdepth 1 -type f -delete
}

EXTRA_OECMAKE = "-DPAHO_WITH_SSL=ON"
