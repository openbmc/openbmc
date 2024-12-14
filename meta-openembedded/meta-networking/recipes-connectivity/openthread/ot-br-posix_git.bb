# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: Apache-2.0
SUMMARY = "OpenThread Border Router"
SECTION = "net"
LICENSE = "BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=87109e44b2fda96a8991f27684a7349c \
                    file://third_party/Simple-web-server/repo/LICENSE;md5=091ac9fd29d87ad1ae5bf765d95278b0 \
                    file://third_party/cJSON/repo/LICENSE;md5=218947f77e8cb8e2fa02918dc41c50d0 \
                    file://third_party/http-parser/repo/LICENSE-MIT;md5=9bfa835d048c194ab30487af8d7b3778 \
                    file://third_party/openthread/repo/LICENSE;md5=543b6fe90ec5901a683320a36390c65f \
                    "
DEPENDS = "autoconf-archive dbus readline avahi jsoncpp boost libnetfilter-queue protobuf protobuf-native"
SRCREV = "b041fa52daaa4dfbf6aa4665d8925c1be0350ca5"
PV = "0.3.0+git"

SRC_URI = "gitsm://github.com/openthread/ot-br-posix.git;protocol=https;branch=main \
           file://0001-otbr-agent.service.in-remove-pre-exec-hook-for-mdns-.patch \
           file://0001-cmake-Disable-nonnull-compare-warning-on-gcc.patch \
           file://default-cxx-std.patch \
           file://0001-fix-build-on-GCC-14-for-yocto.patch;patchdir=third_party/openthread/repo \
           file://0001-Musl-build-fix.patch;patchdir=third_party/openthread/repo \
           "

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "otbr-agent.service"

inherit pkgconfig cmake systemd

LDFLAGS:append:riscv32 = " -latomic"

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF \
                 -DOTBR_DBUS=ON \
                 -DOTBR_REST=ON \
                 -DOTBR_WEB=OFF \
                 -DCMAKE_LIBRARY_PATH=${libdir} \
                 -DOT_POSIX_PRODUCT_CONFIG=${sysconfdir}/openthread.conf.example \
                 -DOT_POSIX_FACTORY_CONFIG=${sysconfdir}/openthread.conf.example \
                 -DOTBR_MDNS=avahi \
                 -DOTBR_BACKBONE_ROUTER=ON \
                 -DOTBR_BORDER_ROUTING=ON \
                 -DOTBR_SRP_ADVERTISING_PROXY=ON \
                 -DOTBR_BORDER_AGENT=ON \
                 -DOT_SPINEL_RESET_CONNECTION=ON \
                 -DOT_TREL=ON \
                 -DOT_MLR=ON \
                 -DOT_SRP_SERVER=ON \
                 -DOT_ECDSA=ON \
                 -DOT_SERVICE=ON \
                 -DOTBR_DUA_ROUTING=ON \
                 -DOT_DUA=ON \
                 -DOT_BORDER_ROUTING_NAT64=ON \
                 -DOTBR_DNSSD_DISCOVERY_PROXY=ON \
                 -DOTBR_INFRA_IF_NAME=eth0 \
                 -DOTBR_NO_AUTO_ATTACH=1 \
                 -DOT_REFERENCE_DEVICE=ON \
                 -DOT_DHCP6_CLIENT=ON \
                 -DOT_DHCP6_SERVER=ON \
                 "
EXTRA_OECMAKE:append:libc-musl = " -DOT_TARGET_MUSL=ON"

RDEPENDS:${PN} = "iproute2 ipset avahi-daemon"

RCONFLICTS:${PN} = "ot-daemon"

FILES:${PN} += "${systemd_unitdir}/*"
FILES:${PN} += "${datadir}/*"
