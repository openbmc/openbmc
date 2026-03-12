DESCRIPTION = "open62541 is an implementation of OPC UA (OPC Unified Architecture)"
HOMEPAGE = "https://github.com/open62541/open62541.git"
LICENSE = "MPL-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad \
    file://deps/mdnsd/LICENSE;md5=3bb4047dc4095cd7336de3e2a9be94f0 \
    file://deps/mqtt-c/LICENSE;md5=9226377baf0b79174c89a1ab55592456 \
"

SRCREV_FORMAT = "opcua_mdnsd_ua-nodeset_mqtt-c"
SRCREV_opcua = "f63e2a819aff6e468242dc2e54ccbd5b75d63654"
SRCREV_mdnsd = "488d24fb9d427aec77df180268f0291eeee7fb8b"
SRCREV_ua-nodeset = "d1bb6a22125bd7cd986272b1ee98a18a91d76fff"
SRCREV_mqtt-c = "0f4c34c8cc00b16cfee094745d68b8cdbaecd8e0"

SRC_URI = " \
    git://github.com/open62541/open62541.git;name=opcua;branch=1.4;protocol=https \
    git://github.com/Pro/mdnsd.git;name=mdnsd;protocol=https;branch=master;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/deps/mdnsd \
    git://github.com/OPCFoundation/UA-Nodeset;name=ua-nodeset;protocol=https;branch=latest;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/deps/ua-nodeset \
    git://github.com/LiamBindle/MQTT-C.git;name=mqtt-c;protocol=https;branch=master;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/deps/mqtt-c \
"


inherit cmake python3native

EXTRA_OECMAKE += "\
    -DBUILD_SHARED_LIBS=ON \
    -DUA_LOGLEVEL=600 \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
"

FILES:${PN} = "${libdir}/lib*${SOLIBS}"

# The tools package contains scripts to generate certificates and OPC UA schema definitions + nodeset descriptions.
PACKAGES += "${PN}-tools"
FILES:${PN}-tools = "${datadir}/${BPN}/*"

PACKAGECONFIG ?= "encryption-mbedtls historizing pubsub pubsub-eth pubsub-informationmodel subscriptions subscriptions-events namespace-full"
PACKAGECONFIG[encryption-mbedtls] = "-DUA_ENABLE_ENCRYPTION=MBEDTLS, , mbedtls, , , encryption-openssl"
PACKAGECONFIG[encryption-openssl] = "-DUA_ENABLE_ENCRYPTION=OPENSSL, , openssl, , , encryption-mbedtls"
PACKAGECONFIG[historizing] = "-DUA_ENABLE_HISTORIZING=ON, -DUA_ENABLE_HISTORIZING=OFF"
PACKAGECONFIG[multithreading] = "-DUA_MULTITHREADING=100, -DUA_MULTITHREADING=0"
PACKAGECONFIG[namespace-full] = "-DUA_NAMESPACE_ZERO=FULL, , , , , namespace-reduced"
PACKAGECONFIG[namespace-reduced] = "-DUA_NAMESPACE_ZERO=REDUCED, , , , , namespace-full"
PACKAGECONFIG[pubsub] = "-DUA_ENABLE_PUBSUB=ON, -DUA_ENABLE_PUBSUB=OFF"
PACKAGECONFIG[pubsub-eth] = "-DUA_ENABLE_PUBSUB_ETH_UADP=ON, -DUA_ENABLE_PUBSUB_ETH_UADP=OFF"
PACKAGECONFIG[pubsub-informationmodel] = "-DUA_ENABLE_PUBSUB_INFORMATIONMODEL=ON, -DUA_ENABLE_PUBSUB_INFORMATIONMODEL=OFF"
PACKAGECONFIG[subscriptions] = "-DUA_ENABLE_SUBSCRIPTIONS=ON, -DUA_ENABLE_SUBSCRIPTIONS=OFF"
PACKAGECONFIG[subscriptions-events] = "-DUA_ENABLE_SUBSCRIPTIONS_EVENTS=ON, -DUA_ENABLE_SUBSCRIPTIONS_EVENTS=OFF"
PACKAGECONFIG[werror] = "-DUA_FORCE_WERROR=ON, -DUA_FORCE_WERROR=OFF"

do_configure:prepend:toolchain-clang:riscv64() {
    sed -i -e 's/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION ON)/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION OFF)/' ${S}/CMakeLists.txt
}

do_configure:prepend:toolchain-clang:riscv32() {
    sed -i -e 's/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION ON)/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION OFF)/' ${S}/CMakeLists.txt
}

do_install:append(){
    sed -i 's|${RECIPE_SYSROOT}|\$\{CMAKE_SYSROOT\}|g' ${D}${libdir}/cmake/open62541/open62541Targets.cmake
}
