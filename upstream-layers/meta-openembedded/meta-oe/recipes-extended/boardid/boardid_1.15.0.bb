SUMMARY = "Print out a platform-specific board serial number"
DESCRIPTION = "This program looks up a device-specific serial number and prints it. \
The original use was to provide some non-changing and unique material for dynamically \
creating device names on a local LAN. There are implementation specific boards as well \
generic mechanisms for getting serial number."
HOMEPAGE = "https://github.com/nerves-project/boardid"
LICENSE = "Apache-2.0 AND CC-BY-4.0 AND CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSES/Apache-2.0.txt;md5=c846ebb396f8b174b10ded4771514fcc \
                    file://LICENSES/CC0-1.0.txt;md5=65d3616852dbf7b1a6d4b53b00626032 \
                    file://LICENSES/CC-BY-4.0.txt;md5=9b33bbd06fb58995fb0e299cd38d1838"

SRC_URI = "git://github.com/nerves-project/boardid/;protocol=https;branch=main;tag=v${PV}"

SRCREV = "2ccf22a8be22d7597e16fb225c1173876674f7a4"

do_install () {
        install -d ${D}${bindir}
        install -m 0755 ${S}/boardid ${D}${bindir}
}
