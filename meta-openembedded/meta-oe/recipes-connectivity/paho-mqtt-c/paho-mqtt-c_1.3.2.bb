SUMMARY = "Paho MQTT - C libraries for the MQTT and MQTT-SN protocols"
DESCRIPTION = "Client implementation of open and standard messaging protocols for Machine-to-Machine (M2M) and Internet of Things (IoT)."
HOMEPAGE = "http://www.eclipse.org/paho/"
SECTION = "console/network"
LICENSE = "EPL-1.0 | EDL-1.0"

LIC_FILES_CHKSUM = " \
        file://edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3 \
        file://epl-v20;md5=d9fc0efef5228704e7f5b37f27192723 \
        file://notice.html;md5=943f861069889acecebf51dfa24478e2 \
        file://about.html;md5=e5662cbb5f8fd5c9faac526e4077898e \
"

SRC_URI = "git://github.com/eclipse/paho.mqtt.c;protocol=http \
           file://0001-Fix-bug-of-free-with-musl.patch"

SRCREV = "fbe39064c4416f879308a8a5390b19d544970789"

DEPENDS = "openssl"

S = "${WORKDIR}/git"

inherit cmake

do_install_append() {
    # paho-mqtt installes some thing that we don't want.
    rm -rf ${D}${prefix}/samples
    find ${D}${prefix} -maxdepth 1 -type f -delete
}

EXTRA_OECMAKE = "-DPAHO_WITH_SSL=ON"

do_configure_prepend_libc-musl() {
    sed -i -e "s/SET(LIBS_SYSTEM c dl pthread anl rt)/SET(LIBS_SYSTEM c dl pthread rt)/g" ${S}/src/CMakeLists.txt 
}
