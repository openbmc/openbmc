SUMMARY = "Paho MQTT - C++ libraries for the MQTT and MQTT-SN protocols"
DESCRIPTION = "Client implementation of open and standard messaging protocols for Machine-to-Machine (M2M) and Internet of Things (IoT)."
HOMEPAGE = "http://www.eclipse.org/paho/"
SECTION = "console/network"
LICENSE = "EPL-2.0 | EDL-1.0"

LIC_FILES_CHKSUM = " \
    file://src/mqtt/message.h;beginline=9;endline=18;md5=c5ceecf5ab99d44dcfaaabdce289071b \
    file://edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3 \
    file://epl-v20;md5=d9fc0efef5228704e7f5b37f27192723 \
"

SRC_URI = "git://github.com/eclipse/paho.mqtt.cpp;protocol=https;branch=master \
           file://0001-cmake-Use-CMAKE_INSTALL_LIBDIR-and-CMAKE_INSTALL_BIN.patch \
"
SRCREV = "4691652479bb4b398c7b81bde639482b164ae6d6"

DEPENDS = "openssl paho-mqtt-c"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DPAHO_WITH_SSL=ON"
